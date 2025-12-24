package com.commons.java8.business.business2;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveTrackDTO implements Serializable {

    private static final long serialVersionUID = -7801100011937851796L;

    /**
     * 场次ID
     */
    private Long liveTrackId;

    /**
     * 业务方ID
     */
    private Long partnerId;

    /**
     * 主播ID
     */
    private Long anchorId;

    /**
     * 直播间ID
     */
    private Long liveStudioId;

    /**
     * 节目开始时间
     */
    private Date realStartTime;

    /**
     * 播控平台
     */
    private Integer playPlatform;

    /**
     * 平台上下线状态
     */
    private Integer platformAvailableStatus;

    /**
     * 平台屏蔽搜索
     */
    private Integer platformRejectSearch;

    /**
     * 平台屏蔽推荐
     */
    private Integer platformRejectRecommend;
}
