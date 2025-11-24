package com.commons.falcon.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StrategyDTO {
	private Long id;
	private String metric;
	private String tags;
	private String func;
	private String op;
	private String rightValue;
}
