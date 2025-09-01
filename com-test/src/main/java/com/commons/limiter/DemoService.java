package com.commons.limiter;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class DemoService {
    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);
    
    @Autowired
    private ExternalApiService externalApiService;
    
    // 组合使用熔断器、重试和舱壁隔离
    @CircuitBreaker(name = "backendA", fallbackMethod = "fallback")
    @Retry(name = "backendA", fallbackMethod = "fallback")
    @Bulkhead(name = "backendA", fallbackMethod = "fallback")
    public String callWithResilience(String param) {
        logger.info("Calling external API with param: {}", param);
        return externalApiService.callExternalApi(param);
    }
    
    // 使用限流器
    @RateLimiter(name = "apiService", fallbackMethod = "rateLimitFallback")
    public String callWithRateLimit(String param) {
        logger.info("Rate limited call with param: {}", param);
        return externalApiService.callExternalApi(param);
    }
    
    // 使用时间限制器（异步方法）
    @TimeLimiter(name = "backendA", fallbackMethod = "timeoutFallback")
    @CircuitBreaker(name = "backendA", fallbackMethod = "timeoutFallback")
    public CompletableFuture<String> callWithTimeout(String param) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Async call with param: {}", param);
            return externalApiService.callExternalApi(param);
        });
    }
    
    // 使用缓存
    /*@CacheResult(name = "userCache", key = "#userId")
    @CircuitBreaker(name = "backendB", fallbackMethod = "cacheFallback")
    public String getUserWithCache(String userId) {
        logger.info("Getting user info (uncached) for: {}", userId);
        return externalApiService.getUserInfo(userId);
    }*/
    
    // Fallback 方法
    private String fallback(String param, Exception e) {
        logger.warn("Fallback triggered for param: {}, due to: {}", param, e.getMessage());
        return "Fallback response for: " + param;
    }
    
    private String rateLimitFallback(String param, Exception e) {
        logger.warn("Rate limit fallback triggered for param: {}", param);
        return "Rate limit exceeded for: " + param;
    }
    
    private CompletableFuture<String> timeoutFallback(String param, Exception e) {
        logger.warn("Timeout fallback triggered for param: {}", param);
        return CompletableFuture.completedFuture("Timeout fallback for: " + param);
    }
    
    private String cacheFallback(String userId, Exception e) {
        logger.warn("Cache fallback triggered for userId: {}", userId);
        return "Cached fallback for: " + userId;
    }
}