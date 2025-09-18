package com.commons.controller;

import com.commons.cache.springcache.cffenine.UserInfo;
import com.commons.cache.springcache.cffenine.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    private final CacheManager cacheManager;

    public CacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Autowired
    private UserService userService;

    /**
     * 获取所有缓存名称
     */
    @GetMapping("/names")
    public Map<String, Object> getCacheNames() {
        Map<String, Object> result = new HashMap<>();
        result.put("cacheNames", cacheManager.getCacheNames());
        return result;
    }

    /**
     * 获取指定缓存的统计信息
     */
    @GetMapping("/{cacheName}/stats")
    public Map<String, Object> getCacheStats(@PathVariable String cacheName) {
        Map<String, Object> result = new HashMap<>();

        if (cacheManager.getCache(cacheName) != null &&
                cacheManager.getCache(cacheName)
                            .getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {

            com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                    (com.github.benmanes.caffeine.cache.Cache<Object, Object>)cacheManager.getCache(cacheName)
                                                                                          .getNativeCache();

            com.github.benmanes.caffeine.cache.stats.CacheStats stats = nativeCache.stats();

            result.put("hitCount", stats.hitCount());
            result.put("missCount", stats.missCount());
            result.put("hitRate", stats.hitRate());
            result.put("missRate", stats.missRate());
            result.put("loadSuccessCount", stats.loadSuccessCount());
            result.put("loadFailureCount", stats.loadFailureCount());
            result.put("totalLoadTime", stats.totalLoadTime());
            result.put("evictionCount", stats.evictionCount());
        } else {
            result.put("error", "Cache not found or not a Caffeine cache");
        }

        return result;
    }

    /**
     * 获取指定缓存的所有键
     */
    @GetMapping("/{cacheName}/keys")
    public Map<String, Object> getCacheKeys(@PathVariable String cacheName) {
        Map<String, Object> result = new HashMap<>();

        if (cacheManager.getCache(cacheName) != null &&
                cacheManager.getCache(cacheName)
                            .getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {

            com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                    (com.github.benmanes.caffeine.cache.Cache<Object, Object>)cacheManager.getCache(cacheName)
                                                                                          .getNativeCache();

            result.put("keys", nativeCache.asMap()
                                          .keySet());
        } else {
            result.put("error", "Cache not found or not a Caffeine cache");
        }

        return result;
    }

    /**
     * 获取指定键的缓存值
     */
    @GetMapping("/{cacheName}/key/{key}")
    public Map<String, Object> getCacheValue(@PathVariable String cacheName, @PathVariable String key) {
        Map<String, Object> result = new HashMap<>();

        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(key);
            if (valueWrapper != null) {
                result.put("value", valueWrapper.get());
            } else {
                result.put("value", null);
            }
        } else {
            result.put("error", "Cache not found");
        }

        return result;
    }

    /**
     * 获取所有缓存名称
     */
    @GetMapping("/getUserById")
    public UserInfo getUserById(Long id) {
        return userService.getUserById(id);
    }

    /**
     * 获取所有缓存名称
     */
    @GetMapping("/deleteUserById")
    public void deleteUserById(Long id) {
        userService.deleteUser(id, null);
    }
}