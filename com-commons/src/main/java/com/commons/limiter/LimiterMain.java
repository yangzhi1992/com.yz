package com.commons.limiter;

import com.google.common.util.concurrent.RateLimiter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

/**
 *
 */
public class LimiterMain {
	public static void main(String[] args) {
		// 每秒100个请求，预热期1秒
		RateLimiter rateLimiter = RateLimiter.create(100);

		// 每秒100个请求，预热期5秒
		RateLimiter.create(100.0, Duration.ofSeconds(5));
	}

	/**
	 * 只能提前1s生产令牌
	 */
	void ratelimiter() {
		/**
		 * qps：100/s，提前1s生产令牌，最大生产100个
		 */
		RateLimiter rateLimiter = RateLimiter.create(100);
		rateLimiter.getRate(); //获取速率，单位时间内产生令牌100.0
		rateLimiter.acquire(); //获取许可，每个key需要1个许可，没有许可会阻塞
		rateLimiter.setRate(10); //设置速率为10，也就是每秒产生10个令牌
		rateLimiter.acquire(10); //获取许可达到10个令牌就放行,否则阻塞
		rateLimiter.tryAcquire(); //获取许可，每个key需要1个许可，没有许可就会返回false,有的话就会返回true
		rateLimiter.tryAcquire(10); //尝试获取10个令牌就返回true，否则返回false
		rateLimiter.tryAcquire(Duration.ofSeconds(2)); //这个方法尝试在指定的时间内（这里是2秒）获取一个许可（permit）：获取到返回true，否则返回false，超时返回false
	}

	/**
	 * 可以预热生产令牌
	 */
	void rateLimiterExtra() throws ReflectiveOperationException {
		/**
		 * 每秒允许的请求数，可看成QPS；
		 * 可看成桶的容量，Guava中最大的突发流量缓冲时间，默认是1s, permitsPerSecond * maxBurstSeconds，就是闲置时累积的缓冲token最大值。
		 */
		double permitsPerSecond = 100;
		double capacity = 200;
		double maxBurstSeconds = new BigDecimal(capacity).divide(new BigDecimal(permitsPerSecond), 1, RoundingMode.HALF_DOWN).doubleValue();
		RateLimiter rateLimiter = RateLimiterUtil.create(permitsPerSecond, maxBurstSeconds);
	}
}
