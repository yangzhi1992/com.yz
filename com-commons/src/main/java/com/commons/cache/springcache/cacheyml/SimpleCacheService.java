package com.commons.cache.springcache.cacheyml;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SimpleCacheService {
    @Cacheable("users")
    public String getUserById(String userId) {
        System.out.println("Fetching from database for userId: " + userId);
        return "User-" + userId;
    }
}
