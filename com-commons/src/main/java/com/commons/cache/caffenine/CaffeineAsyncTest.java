package com.commons.cache.caffenine;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class CaffeineAsyncTest {

    private static String asyncLoadData(String key) {
        // 模拟异步加载数据，例如从数据库或远程服务获取
        try {
            Thread.sleep(1000); // 模拟耗时操作
        } catch (InterruptedException e) {
            Thread.currentThread()
                  .interrupt();
            throw new RuntimeException(e);
        }
        return "Value for " + key;
    }

    public static void main(String[] args) {
        /**
         * 默认情况下，Caffeine 使用 ForkJoinPool.commonPool() 执行异步操作。您可以指定自定义的执行器：
         * ForkJoinPool.commonPool()，这是一个 JVM 范围内的静态共享线程池
         * Java 的 Fork/Join 框架使用这个公共池来执行并行任务
         */
        // 创建异步加载缓存
        AsyncLoadingCache<String, String> cache = Caffeine.newBuilder()
                                                          .maximumSize(100)
                                                          .expireAfterWrite(10, TimeUnit.MINUTES)
                                                          // 构建异步缓存，并指定异步加载器
                                                          .buildAsync(key -> asyncLoadData(key));

        // 获取数据，返回的是 CompletableFuture
        CompletableFuture<String> future = cache.get("key1");
        // 当异步操作完成时，处理结果
        future.thenAccept(data -> {
            System.out.println("Data: " + data);
        });
        // 或者使用异常处理
        future.thenAccept(value -> {
                  System.out.println("成功: " + value);
              })
              .exceptionally(ex -> {
                  System.out.println("失败: " + ex.getMessage());
                  return null;
              });

        // 批量异步获取
        List<String> keys = Arrays.asList("key1", "key2", "key3");
        CompletableFuture<Map<String, String>> futureMap = cache.getAll(keys);
        futureMap.thenAccept(map -> {
            map.forEach((key, value) -> {
                System.out.println(key + " -> " + value);
            });
        });

        // 这会阻塞直到操作完成
        future.join();

        /**
         * 自定义线程池
         */
        Executor customExecutor = Executors.newFixedThreadPool(5);
        AsyncLoadingCache<String, String> asyncCache = Caffeine.newBuilder()
                                                               .maximumSize(10_000)
                                                               .expireAfterWrite(10, TimeUnit.MINUTES)
                                                               .refreshAfterWrite(1, TimeUnit.MINUTES)
                                                               .executor(customExecutor)
                                                               .buildAsync(key -> asyncLoadData(key));

        // 单个键获取
        CompletableFuture<String> future1 = asyncCache.get("user:123");
        future1.thenAccept(value -> {
            System.out.println("单个获取结果: " + value);
        });

        // 批量获取
        List<String> keys2 = Arrays.asList("user:123", "user:456", "user:789");
        CompletableFuture<Map<String, String>> futureMap2 = asyncCache.getAll(keys2);
        futureMap.thenAccept(map -> {
            System.out.println("批量获取结果:");
            map.forEach((k, v) -> System.out.println("  " + k + " -> " + v));
        });

        // 等待所有异步操作完成
        CompletableFuture.allOf(future1, futureMap2)
                         .join();
    }
}
