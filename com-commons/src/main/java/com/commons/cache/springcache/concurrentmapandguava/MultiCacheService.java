package com.commons.cache.springcache.concurrentmapandguava;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class MultiCacheService {

    @Cacheable(value = "users", cacheManager = "concurrentMapCacheManager") // 使用 ConcurrentHashMap 缓存
    public String getUser(String userId) {
        return "User-" + userId;
    }

    @Cacheable(value = "products", cacheManager = "guavaCacheManager") // 使用 Guava 缓存
    public String getProduct(String productId) {
        return "Product-" + productId;
    }
}
