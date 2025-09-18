package com.commons.cache.ehcache;

import java.time.Duration;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;

/**
 * 直接在代码中配置缓存
 */
public class EhcacheExample {
    public static void main(String[] args) {
        // 1. 创建 CacheManager
        CacheManager cacheManager =
                CacheManagerBuilder.newCacheManagerBuilder()
                                   .withCache("usersCache",
                                           CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                                                            Long.class, //key 类型
                                                                            String.class, //value 类型
                                                                            ResourcePoolsBuilder.newResourcePoolsBuilder()
                                                                                                .heap(10,
                                                                                                        EntryUnit.ENTRIES) //内存
                                                                                                .offheap(1,
                                                                                                        MemoryUnit.MB)    //堆外内存
                                                                                                .disk(20,
                                                                                                        MemoryUnit.MB,
                                                                                                        true)
                                                                    )
                                                                    .withExpiry(
                                                                            ExpiryPolicyBuilder.timeToLiveExpiration(
                                                                                    Duration.ofSeconds(
                                                                                            30)))
                                                                    .withExpiry(
                                                                            ExpiryPolicyBuilder.timeToIdleExpiration(
                                                                                    Duration.ofSeconds(
                                                                                            30)))
                                   ) // 存活时间为 30 秒
                                   .build(true);
        cacheManager.init();
        Cache<Long, String> preConfigured =
                cacheManager.getCache("preConfigured", Long.class, String.class);
        preConfigured.put(1L, "da one!");
        System.out.println(preConfigured.get(1L));

        Cache<Long, String> myCache = cacheManager.createCache("myCache",
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
                        ResourcePoolsBuilder.heap(10)));

        myCache.put(1L, "da one!");
        System.out.println(myCache.get(1L));

        cacheManager.removeCache("preConfigured");
        cacheManager.close();
    }
}