package com.commons.disruptor.batchAndTime;

import com.commons.disruptor.MessageEvent;
import com.google.common.util.concurrent.RateLimiter;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.LifecycleAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 批量事件处理器
 * 支持按批次大小和定时两种方式触发flush操作
 */
public class MessageBatchEventHandler implements EventHandler<MessageEvent>, LifecycleAware {

	private static final Logger logger = LoggerFactory.getLogger(MessageBatchEventHandler.class);

	private static final int DEFAULT_BATCH_SIZE = 100;
	private static final long DEFAULT_FLUSH_INTERVAL_MILLIS = 1000L;
	private static final int INITIAL_BATCH_CAPACITY = 128;
	private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

	private final RateLimiter rateLimiter;
	private final int batchSize;
	private final long flushIntervalMillis;

	// 批量数据缓存，预设初始容量减少扩容开销
	private final List<MessageEvent> batch = new ArrayList<>(INITIAL_BATCH_CAPACITY);
	
	// 批量数据锁
	private final Lock batchLock = new ReentrantLock();
	
	/**
	 * 关键优化点：标记是否正在执行flush操作
	 * 使用 AtomicBoolean 确保原子性，防止批量和定时任务同时执行flush
	 */
	private final AtomicBoolean isFlushing = new AtomicBoolean(false);

	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> future;
	private final AtomicBoolean isShutdown = new AtomicBoolean(false);

	public MessageBatchEventHandler(RateLimiter rateLimiter) {
		this(rateLimiter, DEFAULT_BATCH_SIZE, DEFAULT_FLUSH_INTERVAL_MILLIS);
	}

	public MessageBatchEventHandler(RateLimiter rateLimiter, int batchSize, long flushIntervalMillis) {
		if (rateLimiter == null) {
			throw new IllegalArgumentException("RateLimiter cannot be null");
		}
		if (batchSize <= 0) {
			throw new IllegalArgumentException("Batch size must be greater than 0");
		}
		if (flushIntervalMillis <= 0) {
			throw new IllegalArgumentException("Flush interval must be greater than 0");
		}
		this.rateLimiter = rateLimiter;
		this.batchSize = batchSize;
		this.flushIntervalMillis = flushIntervalMillis;
	}

	@Override
	public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) {
		if (isShutdown.get() || event == null) {
			return;
		}

		batchLock.lock();
		try {
			batch.add(event);

			if (batch.size() >= batchSize) {
				flushIfNotBusy("batch");
			}
		} catch (Exception e) {
			logger.error("Error processing event", e);
		} finally {
			batchLock.unlock();
		}
	}

	/**
	 * 尝试执行flush操作（如果当前没有正在flush）
	 * 
	 * @param source 标识flush来源（batch/schedule/shutdown），用于日志追踪
	 */
	private void flushIfNotBusy(String source) {
		if (isFlushing.compareAndSet(false, true)) {
			try {
				rateLimiter.acquire();
				flushInternal(source);
			} catch (Exception e) {
				logger.error("Error during flush from source: {}", source, e);
			} finally {
				resetFlushingFlag();
			}
		}
	}

	/**
	 * 执行flush操作
	 * 
	 * @param source 标识flush来源（batch/schedule/shutdown），用于日志追踪
	 */
	private void flushInternal(String source) {
		if (batch.isEmpty()) {
			return;
		}

		List<MessageEvent> batchToFlush;
		batchLock.lock();
		try {
			if (batch.isEmpty()) {
				return;
			}
			// 创建副本并清空原列表，减少锁持有时间
			batchToFlush = new ArrayList<>(batch);
			batch.clear();
		} finally {
			batchLock.unlock();
		}

		// 在锁外处理数据，减少锁持有时间
		processBatch(batchToFlush, source);
	}

	/**
	 * 处理批次数据
	 * 
	 * @param batchToFlush 待处理的数据批次
	 * @param source flush来源
	 */
	protected void processBatch(List<MessageEvent> batchToFlush, String source) {
		// 子类可以重写此方法来实现具体的业务逻辑
		if (logger.isDebugEnabled()) {
			logger.debug("Processing batch of {} events from source: {}", batchToFlush.size(), source);
		}
		// TODO: 实现具体的业务处理逻辑
		// 例如：批量写入数据库、发送到消息队列等
	}

	/**
	 * 重置flush标志
	 */
	private void resetFlushingFlag() {
		isFlushing.set(false);
	}

	@Override
	public void onStart() {
		scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread thread = new Thread(r, "MessageEventHandler-FlushTimer");
			thread.setDaemon(true);
			return thread;
		});

		future = scheduler.scheduleAtFixedRate(this::scheduledFlush, 
			flushIntervalMillis, flushIntervalMillis, TimeUnit.MILLISECONDS);
	}

	/**
	 * 定时flush任务
	 */
	private void scheduledFlush() {
		try {
			if (isFlushing.compareAndSet(false, true)) {
				if (batchLock.tryLock()) {
					try {
						if (!batch.isEmpty()) {
							rateLimiter.acquire();
							flushInternal("schedule");
						}
					} finally {
						batchLock.unlock();
						resetFlushingFlag();
					}
				} else {
					// 如果获取不到锁，说明事件处理线程正在操作batch
					// 释放flush标志，让事件处理线程继续（如果它需要flush的话）
					resetFlushingFlag();
				}
			}
		} catch (Exception e) {
			logger.error("Error in scheduled flush", e);
			resetFlushingFlag();
		}
	}

	@Override
	public void onShutdown() {
		isShutdown.set(true);

		// 取消定时任务
		if (future != null) {
			future.cancel(false);
		}

		// 关闭调度器
		shutdownScheduler();

		// 处理剩余数据
		batchLock.lock();
		try {
			if (!batch.isEmpty()) {
				// shutdown时不需要限流，直接flush
				flushInternal("shutdown");
			}
		} catch (Exception e) {
			logger.error("Error flushing remaining events during shutdown", e);
		} finally {
			batchLock.unlock();
		}
	}

	/**
	 * 关闭调度器
	 */
	private void shutdownScheduler() {
		if (scheduler == null) {
			return;
		}

		scheduler.shutdown();
		try {
			if (!scheduler.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
				logger.warn("Scheduler did not terminate within {} seconds, forcing shutdown", SHUTDOWN_TIMEOUT_SECONDS);
				scheduler.shutdownNow();
				// 再次等待确保真正关闭
				if (!scheduler.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
					logger.error("Scheduler did not terminate after shutdownNow()");
				}
			}
		} catch (InterruptedException e) {
			logger.warn("Interrupted while waiting for scheduler to terminate", e);
			scheduler.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}

