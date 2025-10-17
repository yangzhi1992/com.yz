package com.commons.monitor.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocketMessage {

    /**
     * 消息类型
     */
    private String messageType;
    /**
     * 消息发送者id
     */
    private String userId;
    /**
     * 消息内容
     */
    private Map<String, String> message;

}
