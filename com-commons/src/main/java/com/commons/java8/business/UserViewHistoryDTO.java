package com.commons.java8.business;

import lombok.Data;

@Data
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
}
