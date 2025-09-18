package com.commons.cache.springcache.java.concurrentmap;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class ConcurrentHashMapCacheConfig {

    // 配置 CacheManager
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("usersCache", "ordersCache");
    }
}
