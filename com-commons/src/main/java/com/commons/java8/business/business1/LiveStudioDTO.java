package com.commons.java8.business.business1;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
