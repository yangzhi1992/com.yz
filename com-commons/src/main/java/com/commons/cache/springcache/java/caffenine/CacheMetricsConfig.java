package com.commons.cache.springcache.java.caffenine;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 缓存统计监控配置-prometheus配置
 */
@Configuration
@EnableScheduling
public class CacheMetricsConfig {

    private final CacheManager cacheManager;
    private final MeterRegistry meterRegistry;

    public CacheMetricsConfig(CacheManager cacheManager, MeterRegistry meterRegistry) {
        this.cacheManager = cacheManager;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void bindCacheToMetrics() {
        // 为所有Caffeine缓存绑定指标
        cacheManager.getCacheNames().forEach(cacheName -> {
            CaffeineCache cache = (CaffeineCache) cacheManager.getCache(cacheName);
            if (cache != null) {
                CaffeineCacheMetrics.monitor(meterRegistry, cache.getNativeCache(), cacheName);
            }
        });
    }

    // 定期打印缓存统计信息（可选）
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void logCacheStats() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            CaffeineCache cache = (CaffeineCache) cacheManager.getCache(cacheName);
            if (cache != null) {
                CacheStats stats = cache.getNativeCache().stats();
                System.out.printf("缓存 %s 统计: %s%n", cacheName, stats.toString());

                // 记录到监控系统
                meterRegistry.gauge("cache.hit.count1", stats.hitCount());
                meterRegistry.gauge("cache.miss.count1", stats.missCount());
                meterRegistry.gauge("cache.hit.rate1", stats.hitRate());
                meterRegistry.gauge("cache.miss.rate1", stats.missRate());
                meterRegistry.gauge("cache.load.success.count1", stats.loadSuccessCount());
                meterRegistry.gauge("cache.load.failure.count1", stats.loadFailureCount());
                meterRegistry.gauge("cache.eviction.count1", stats.evictionCount());
            }
        });
    }
}