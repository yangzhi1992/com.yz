package com.commons.disruptor;

import com.commons.disruptor.batchAndTime.MessageBatchEventHandler;
import com.commons.disruptor.single.MessageEventHandler;
import com.google.common.util.concurrent.RateLimiter;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.util.List;

/**
 * 初始化 Disruptor，发布事件
 * 1. 多种等待策略
 * BlockingWaitStrategy：使用锁和条件变量，CPU使用率低但延迟高
 * SleepingWaitStrategy：先自旋然后使用Thread.yield()，平衡性能和CPU使用
 * YieldingWaitStrategy：低延迟但高CPU使用率，适合高性能系统
 * BusySpinWaitStrategy：最高性能但CPU使用率最高，避免用于虚拟环境
 */
public class DisruptorConfig {
    private static final int BUFFER_SIZE = 1024 * 1024 * 8;

    private Disruptor<MessageEvent> disruptor;
    private RingBuffer<MessageEvent> ringBuffer;

    /**
     * 初始化 Disruptor
     *
     * @param rateLimiter
     */
    public DisruptorConfig(RateLimiter rateLimiter) {
        // 创建disruptor
        this.disruptor = new Disruptor<>(new MessageEventFactory(), BUFFER_SIZE, DaemonThreadFactory.INSTANCE, ProducerType.MULTI, new BlockingWaitStrategy());

        // 设置事件处理器

        // single
        this.disruptor.handleEventsWith(new MessageEventHandler(rateLimiter));
        // batchAndTime
        this.disruptor.handleEventsWith(new MessageBatchEventHandler(rateLimiter));

        // 1. 独立消费模式（每个事件被所有处理器处理）（广播模式）
        //this.disruptor.handleEventsWith(new MessageEventHandler(rateLimiter),new MessageEventHandler(rateLimiter));

        // 2. 顺序消费模式（handler1处理完才交给handler2）（管道模式）
        //disruptor.handleEventsWith(new MessageEventHandler(rateLimiter)).then(new MessageEventHandler(rateLimiter));

        // 启动disruptor
        this.ringBuffer = disruptor.start();
    }

    /**
     * 发布单个事件
     *
     * @param messageEvent
     */
    public void publishEvent(MessageEvent messageEvent) {
        long sequence = ringBuffer.next();
        try {
            MessageEvent event = ringBuffer.get(sequence);
            event.setId(messageEvent.getId());
            event.setUserId(messageEvent.getUserId());
            event.setPartnerId(messageEvent.getPartnerId());
            event.setLiveTrackId(messageEvent.getLiveTrackId());
            event.setStudioId(messageEvent.getStudioId());
            event.setCreateTime(messageEvent.getCreateTime());
            event.setUpdateTime(messageEvent.getUpdateTime());
            event.setAnchorId(messageEvent.getAnchorId());
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    /**
     * 批量发布多个事件
     *
     * @param messageEvents
     */
    public void publishBatch(List<MessageEvent> messageEvents) {
        // 获取批量序列号
        long hi = ringBuffer.next(messageEvents.size());
        long lo = hi - (messageEvents.size() - 1);

        try {
            for (long seq = lo; seq <= hi; seq++) {
                MessageEvent event = ringBuffer.get(seq);
                event = messageEvents.get((int) (seq - lo));
            }
        } finally {
            // 批量发布
            ringBuffer.publish(lo, hi);
        }
    }

    public void shutdown() {
        disruptor.shutdown();
    }
}
