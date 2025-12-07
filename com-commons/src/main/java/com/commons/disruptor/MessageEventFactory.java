package com.commons.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * 创建事件工厂
 */
public class MessageEventFactory implements EventFactory<MessageEvent> {
    @Override
    public MessageEvent newInstance() {
        return MessageEvent.builder()
                .build();
    }
}
