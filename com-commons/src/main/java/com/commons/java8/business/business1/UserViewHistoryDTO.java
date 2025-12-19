package com.commons.java8.business.business1;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserViewHistoryDTO {
	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * 直播间id
	 */
	private Long liveStudioId;

	/**
	 * 观看时长
	 */
	private Long viewTime;

	/**
	 * 主播
	 */
	private Long anchorId;

	/**
	 * 观看数量
	 */
	private Integer viewCount;

	/**
	 * 观看时间
	 */
	private Long lastViewTime;
}
