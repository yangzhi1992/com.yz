package com.commons.java8.business;

import lombok.Data;

@Data
public class LiveStudioDTO {
	/**
	 * 主播id
	 */
	private Long anchorId;

	/**
	 * 直播间id
	 */
	private Long liveStudioId;
}
