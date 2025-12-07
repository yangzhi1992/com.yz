package com.commons.disruptor.batch;

import com.commons.disruptor.MessageEvent;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.LifecycleAware;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 批量消费模式 - 达到数量批量处理，或者时间批量处理模式
 */
public class TimedBatchHandler implements EventHandler<MessageEvent>, LifecycleAware {
    private List<MessageEvent> batch = new ArrayList<>();
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> future;
    private volatile boolean flushRequested = false;
    
    @Override
    public void onStart() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        future = scheduler.scheduleAtFixedRate(() -> {
            flushRequested = true;
        }, 100, 100, TimeUnit.MILLISECONDS); // 每100ms强制刷新一次
    }
    
    @Override
    public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) {
        batch.add(event);
        
        if (batch.size() >= 1000 || endOfBatch || flushRequested) {
            flush();
        }
    }
    
    private void flush() {
        if (!batch.isEmpty()) {
            // 处理批量数据
            System.out.println("Flushing batch of " + batch.size() + " events");
            batch.clear();
        }
        flushRequested = false;
    }
    
    @Override
    public void onShutdown() {
        future.cancel(true);
        scheduler.shutdown();
        flush(); // 最后处理剩余数据
    }
}
