package com.commons.cache.springcache.concurrentmap;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class MultiCacheService {

    @Cacheable(value = "users", cacheManager = "concurrentMapCacheManager") // 使用 ConcurrentHashMap 缓存
    public String getUser(String userId) {
        return "User-" + userId;
    }
}
