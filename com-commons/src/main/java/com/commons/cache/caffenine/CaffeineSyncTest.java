package com.commons.cache.caffenine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class CaffeineSyncTest {

    private static String fetchFromDatabase(String key) {
        // 模拟从数据库获取数据
        System.out.println("Fetching from database for key: " + key);
        try {
            Thread.sleep(500); // 模拟耗时操作
        } catch (InterruptedException e) {
            Thread.currentThread()
                  .interrupt();
        }
        return "Value for " + key;
    }

    public static void main(String[] args) {
        LoadingCache<String, String> cache = Caffeine.newBuilder()
                                                     .maximumSize(1000) // 最大容量
                                                     .expireAfterWrite(10, TimeUnit.MINUTES) // 写入后10分钟过期
                                                     .refreshAfterWrite(1, TimeUnit.MINUTES) // 写入1分钟后刷新
                                                     .removalListener(
                                                             (String key, String value, RemovalCause cause) -> {  // 缓存移除时的回调
                                                                 System.out.printf("Key %s was removed (%s)%n", key,
                                                                         cause);
                                                             })
                                                     .evictionListener(
                                                             (String key, Object value, RemovalCause cause) -> { // 缓存淘汰时的回调
                                                                 // 缓存淘汰时的回调
                                                                 System.out.println(
                                                                         "Key " + key + " was evicted due to " + cause);
                                                             })
                                                     .recordStats()                                   // 开启统计
                                                     .build(key -> fetchFromDatabase(key));     // 提供加载函数根据key加载数据

        // 第一次获取，会从fetchFromDatabase方法加载-懒加载（自动加载）
        System.out.println(cache.get("key1"));
        // 第二次获取，从缓存获取
        System.out.println(cache.get("key1"));
        // 打印统计信息
        System.out.println(cache.stats());

        // 手动加载缓存
        cache.put("key1", "value1");
        System.out.println(cache.get("key1"));

        // 移除单个缓存
        cache.invalidate("key1");
        System.out.println(cache.get("key1"));

        // 批量获取缓存
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        System.out.println(cache.getAll(Arrays.asList("key2", "key3")));

        // 移除所有缓存
        cache.invalidateAll();

        /************ 淘汰策略：基于大小淘汰,基于时间淘汰 ************/
        // 基于条目数量
        Cache<String, Object> numCache = Caffeine.newBuilder()
                                                 .maximumSize(10_000)
                                                 .build();

        // 基于权重（适用于值大小不同的情况）
        Cache<String, Object> weightedCache = Caffeine.newBuilder()
                                                      .maximumWeight(10_000_000) // 最大权重
                                                      .weigher((String key, Object value) -> {
                                                          // 自定义权重计算逻辑
                                                          if (value instanceof String) {
                                                              return ((String)value).length();
                                                          }
                                                          return 1;
                                                      })
                                                      .build();
        // 写入后固定时间过期
        Cache<String, Object> writeCache = Caffeine.newBuilder()
                                                   .expireAfterWrite(10, TimeUnit.MINUTES)
                                                   .build();

        // 访问后固定时间过期
        Cache<String, Object> queryCache = Caffeine.newBuilder()
                                                   .expireAfterAccess(10, TimeUnit.MINUTES)
                                                   .build();

        // 自定义过期策略
        Cache<String, Object> cache3 = Caffeine.newBuilder()
                                               .expireAfter(new Expiry<String, Object>() {
                                                   @Override
                                                   public long expireAfterCreate(String key, Object value,
                                                           long currentTime) {
                                                       // 创建后过期时间
                                                       return TimeUnit.SECONDS.toNanos(30);
                                                   }

                                                   @Override
                                                   public long expireAfterUpdate(String key, Object value,
                                                           long currentTime, long currentDuration) {
                                                       // 更新后过期时间
                                                       return currentDuration;
                                                   }

                                                   @Override
                                                   public long expireAfterRead(String key, Object value,
                                                           long currentTime, long currentDuration) {
                                                       // 读取后过期时间
                                                       return currentDuration;
                                                   }
                                               })
                                               .build();
        /************ 淘汰策略：基于大小淘汰,基于时间淘汰 ************/

    }
}
