package com.commons.timer.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExample {
    public static void main(String[] args) throws InterruptedException {
        // 创建具有2个核心线程的调度线程池
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        
        System.out.println("ScheduledThreadPool示例开始...");
        
        // 示例1：一次性延迟任务
        executor.schedule(() -> {
            System.out.println("一次性任务执行: " + System.currentTimeMillis());
        }, 2, TimeUnit.SECONDS);
        
        // 示例2：固定速率任务
        executor.scheduleAtFixedRate(() -> {
            long startTime = System.currentTimeMillis();
            System.out.println("固定速率任务开始: " + startTime);
            try {
                // 模拟任务执行时间
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("固定速率任务结束, 耗时: " + 
                (System.currentTimeMillis() - startTime) + "ms");
        }, 1, 2, TimeUnit.SECONDS);
        
        // 示例3：固定延迟任务
        executor.scheduleWithFixedDelay(() -> {
            long startTime = System.currentTimeMillis();
            System.out.println("固定延迟任务开始: " + startTime);
            try {
                // 模拟任务执行时间
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("固定延迟任务结束, 耗时: " + 
                (System.currentTimeMillis() - startTime) + "ms");
        }, 1, 2, TimeUnit.SECONDS);
        
        // 运行一段时间后关闭线程池
        Thread.sleep(10000);
        System.out.println("关闭线程池...");
        executor.shutdown();
        
        // 等待所有任务完成
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}