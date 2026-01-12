package com.commons.limiter.leakyBucket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
	//桶事件
	private BucketEvent event;
	//插入队列的时间
	private long timestamp;
}
