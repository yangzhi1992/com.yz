package com.commons.cache.springcache.concurrentmap;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class MultiCacheConfig {

    @Bean(name = "concurrentMapCacheManager")
    public CacheManager concurrentMapCacheManager() {
        return new ConcurrentMapCacheManager("users");
    }
}
