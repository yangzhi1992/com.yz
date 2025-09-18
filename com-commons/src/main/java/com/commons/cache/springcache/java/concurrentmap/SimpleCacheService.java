package com.commons.cache.springcache.java.concurrentmap;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SimpleCacheService {
    @Cacheable(value = "usersCache", key = "#userId")
    public String getUserById(String userId) {
        System.out.println("Fetching user from database for ID: " + userId);
        return "User-" + userId;
    }
}
