package com.commons.disruptor.batch;

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

public class MessageEventHandler implements EventHandler<MessageEvent>, LifecycleAware {
	private final RateLimiter rateLimiter;
	private final int batchSize = 100;
	private final long flushIntervalMillis = 100;

	private final List<MessageEvent> batch = new ArrayList<>();
	private final Lock batchLock = new ReentrantLock();

	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> future;
	private final AtomicBoolean isShutdown = new AtomicBoolean(false);

	public MessageEventHandler(
			RateLimiter rateLimiter
	) {
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

			boolean shouldFlush = batch.size() >= batchSize || endOfBatch;

			if (shouldFlush) {
				flushInternal();
			}
		} finally {
			batchLock.unlock();
		}
	}

	private void flushInternal() {
		if (batch.isEmpty()) {
			return;
		}

		try {
			rateLimiter.acquire();
		} catch (Exception e) {
		} finally {
			batch.clear();
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
				if (!batch.isEmpty()) {
					batchLock.lock();
					try {
						if (!batch.isEmpty()) {
							flushInternal();
						}
					} finally {
						batchLock.unlock();
					}
				}
			} catch (Exception e) {
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
				Thread.currentThread()
						.interrupt();
			}
		}

		// 最后处理剩余数据
		batchLock.lock();
		try {
			if (!batch.isEmpty()) {
				flushInternal();
			}
		} finally {
			batchLock.unlock();
		}
	}
}