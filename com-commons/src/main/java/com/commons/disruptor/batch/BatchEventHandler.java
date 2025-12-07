package com.commons.disruptor.batch;

import com.commons.disruptor.MessageEvent;
import com.lmax.disruptor.EventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量消费模式 - 达到数量批量处理
 */
public class BatchEventHandler implements EventHandler<MessageEvent> {
    private List<MessageEvent> batch = new ArrayList<>();
    private static final int BATCH_SIZE = 100;

    @Override
    public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) {
        batch.add(event);

        // 当达到批量大小或是一批的最后一个事件时处理
        if (batch.size() >= BATCH_SIZE || endOfBatch) {
            processBatch(batch);
            batch.clear();
        }
    }

    private void processBatch(List<MessageEvent> batch) {
        // 执行批量操作，如批量写入数据库
        System.out.println("Processing batch of " + batch.size() + " events");
    }
}
