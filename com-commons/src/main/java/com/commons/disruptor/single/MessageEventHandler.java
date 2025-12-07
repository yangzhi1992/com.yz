package com.commons.disruptor.single;

import com.commons.disruptor.MessageEvent;
import com.google.common.util.concurrent.RateLimiter;
import com.lmax.disruptor.EventHandler;

/**
 * 定义事件处理器
 */
public class MessageEventHandler implements EventHandler<MessageEvent> {
    private final RateLimiter rateLimiter;

    public MessageEventHandler(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) {
        try {
            rateLimiter.acquire();
        } catch (Exception e) {
        }
    }
}
