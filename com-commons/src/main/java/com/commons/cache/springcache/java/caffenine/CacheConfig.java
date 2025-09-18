package com.commons.cache.springcache.java.caffenine;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching  // 启用Spring缓存注解
public class CacheConfig {

    /**
     * 单个缓存
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());                // 开启统计
        // 设置缓存名称（可选）
        cacheManager.setCacheNames(Arrays.asList("users", "products", "orders"));
        return cacheManager;
    }

    /**
     * 多缓存配置
     *//*
    @Bean
    public CacheManager cacheManager2() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        // 创建不同的缓存配置
        Cache usersCache = new CaffeineCache("users",
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterWrite(1, TimeUnit.HOURS)
                        .build());

        Cache productsCache = new CaffeineCache("products",
                Caffeine.newBuilder()
                        .maximumSize(5000)
                        .expireAfterAccess(30, TimeUnit.MINUTES)
                        .build());

        Cache ordersCache = new CaffeineCache("orders",
                Caffeine.newBuilder()
                        .maximumSize(5000)
                        .expireAfterAccess(30, TimeUnit.MINUTES)
                        .build());

        Cache configurationsCache = new CaffeineCache("configurations",
                Caffeine.newBuilder()
                        .maximumSize(5000)
                        .expireAfterAccess(30, TimeUnit.MINUTES)
                        .build());

        cacheManager.setCaches(Arrays.asList(usersCache, productsCache,ordersCache,configurationsCache));
        return cacheManager;
    }

    *//**
     * 高级配置：自定义缓存加载器
     *//*
    @Bean
    public CacheManager cacheManager3() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());

        // 设置自定义缓存加载器
        cacheManager.setCacheLoader(cacheLoader());

        return cacheManager;
    }*/

    private CacheLoader<Object, Object> cacheLoader() {
        return new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) throws Exception {
                // 当缓存未命中时，调用此方法加载数据
                return loadDataFromDatabase(key);
            }
        };
    }

    private Object loadDataFromDatabase(Object key) {
        // 实现从数据库加载单条数据的逻辑
        return null;
    }

    private Map<Object, Object> batchLoadDataFromDatabase(Iterable<?> keys) {
        // 实现从数据库批量加载数据的逻辑
        return new HashMap<>();
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                       .initialCapacity(100)           // 初始容量
                       .maximumSize(500)               // 最大容量
                       .expireAfterAccess(10, TimeUnit.MINUTES)  // 访问后10分钟过期
                       .recordStats();                 // 开启统计
    }
}