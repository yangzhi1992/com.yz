package com.commons.cache.springcache.concurrentmapandguava;

import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class MultiCacheConfig {

    @Bean(name = "concurrentMapCacheManager")
    public CacheManager concurrentMapCacheManager() {
        return new ConcurrentMapCacheManager("users");
    }

    @Bean(name = "guavaCacheManager")
    public CacheManager guavaCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new GuavaCache("products", CacheBuilder.newBuilder()
                                                       .expireAfterWrite(10, TimeUnit.MINUTES)
                                                       .maximumSize(100)
                                                       .build())
        ));
        return cacheManager;
    }
}
