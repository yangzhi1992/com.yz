package com.commons.disruptor.batchAndTime;

import com.commons.disruptor.MessageEvent;
import com.google.common.util.concurrent.RateLimiter;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.LifecycleAware;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageBatchEventHandler implements EventHandler<MessageEvent>, LifecycleAware {

	private final RateLimiter rateLimiter;
	
	private final int batchSize = 100;

	private final long flushIntervalMillis = 1000;

	// 批量数据缓存
	private final List<MessageEvent> batch = new ArrayList<>();
	
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
		this.rateLimiter = rateLimiter;
	}

	@Override
	public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) {
		if (isShutdown.get()) {
			return;
		}

		batchLock.lock();
		try {
			batch.add(event);

			boolean shouldFlush = batch.size() >= batchSize;

			if (shouldFlush) {
				if (isFlushing.compareAndSet(false, true)) {
					try {
						rateLimiter.acquire();
						flushInternal("batch");
					} finally {
						isFlushing.set(false);
					}
				}
			}
		} finally {
			batchLock.unlock();
		}
	}

	/**
	 * 执行flush操作
	 * 
	 * @param flag 标识flush来源（batch/schedule/shutdown），用于日志追踪
	 */
	private void flushInternal(String flag) {
		if (batch.isEmpty()) {
			return;
		}

		try {
			List<MessageEvent> batchToFlush = new ArrayList<>(batch);
			batch.clear();
		} catch (Exception e) {
		}
	}

	@Override
	public void onStart() {
		scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread thread = new Thread(r, "MessageEventHandler-FlushTimer");
			thread.setDaemon(true);
			return thread;
		});

		future = scheduler.scheduleAtFixedRate(() -> {
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
							isFlushing.set(false);
						}
					} else {
						// 如果获取不到锁，说明事件处理线程正在操作batch
						// 释放flush标志，让事件处理线程继续（如果它需要flush的话）
						isFlushing.set(false);
					}
				}
			} catch (Exception e) {
				isFlushing.set(false);
			}
		}, flushIntervalMillis, flushIntervalMillis, TimeUnit.MILLISECONDS);
	}

	@Override
	public void onShutdown() {
		isShutdown.set(true);

		// 取消定时任务
		if (future != null) {
			future.cancel(false);
		}

		// 关闭调度器
		if (scheduler != null) {
			scheduler.shutdown();
			try {
				if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
					scheduler.shutdownNow();
				}
			} catch (InterruptedException e) {
				scheduler.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}

		batchLock.lock();
		try {
			if (!batch.isEmpty()) {
				flushInternal("shutdown");
			}
		} finally {
			batchLock.unlock();
		}
	}
}

