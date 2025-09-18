package com.commons.cache.springcache.cacheyml;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EhcacheCacheService {
    @Cacheable("orders")
    public String getUserInfo(String userId) {
        System.out.println("Fetching user info from database for ID: " + userId);
        return "UserInfo-" + userId;
    }
}
