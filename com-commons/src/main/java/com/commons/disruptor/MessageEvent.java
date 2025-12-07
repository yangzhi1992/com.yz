package com.commons.disruptor;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 定义事件
 */
@Data
@Builder
public class MessageEvent {
    private Long id;
    private Long partnerId;
    private Long userId;
    private Long studioId;
    private Long liveTrackId;
    private Date createTime;
    private Date updateTime;
    private Long anchorId;
}
