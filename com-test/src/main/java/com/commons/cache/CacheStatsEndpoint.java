package com.commons.cache;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建自定义的缓存监控端点：
 */
@Component
@Endpoint(id = "cachestats")
public class CacheStatsEndpoint {
    
    private final CacheManager cacheManager;
    
    public CacheStatsEndpoint(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    @ReadOperation
    public Map<String, Map<String, Object>> cacheStats() {
        Map<String, Map<String, Object>> result = new HashMap<>();
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            CaffeineCache cache = (CaffeineCache) cacheManager.getCache(cacheName);
            if (cache != null) {
                CacheStats stats = cache.getNativeCache().stats();
                Map<String, Object> cacheStats = new HashMap<>();
                
                cacheStats.put("hitCount", stats.hitCount());
                cacheStats.put("missCount", stats.missCount());
                cacheStats.put("hitRate", stats.hitRate());
                cacheStats.put("missRate", stats.missRate());
                cacheStats.put("loadSuccessCount", stats.loadSuccessCount());
                cacheStats.put("loadFailureCount", stats.loadFailureCount());
                cacheStats.put("totalLoadTime", stats.totalLoadTime());
                cacheStats.put("evictionCount", stats.evictionCount());
                
                result.put(cacheName, cacheStats);
            }
        });
        
        return result;
    }
}