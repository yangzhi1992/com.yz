package com.commons.timer.thread;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程工厂实现监控和错误日志功能
 */
public class CustomThreadPoolWithMonitor {
    public static void main(String[] args) {
        // 创建自定义线程池
        EnhancedThreadPool executor = new EnhancedThreadPool(2);

        // 提交任务
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                System.out.println(Thread.currentThread().getName() + ": 开始工作");
                throw new RuntimeException("任务发生异常！");
            });
        }

        executor.shutdown();
    }
}

// 自定义线程池类
class EnhancedThreadPool extends ThreadPoolExecutor {
    private final AtomicInteger numberOfTasks = new AtomicInteger(0);

    public EnhancedThreadPool(int corePoolSize) {
        super(corePoolSize, corePoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new CustomThreadFactory());
    }

    @Override
    protected void beforeExecute(Thread thread, Runnable r) {
        super.beforeExecute(thread, r);
        System.out.println(thread.getName() + ": 任务开始");
        numberOfTasks.incrementAndGet(); // 任务计数
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        numberOfTasks.decrementAndGet(); // 任务完成，计数减1
        if (t != null) {
            // 记录异常
            System.err.println(Thread.currentThread().getName() + " 出现异常: " + t.getMessage());
        }
        System.out.println(Thread.currentThread().getName() + ": 任务结束");
    }

    @Override
    protected void terminated() {
        super.terminated();
        System.out.println("线程池已关闭");
        System.out.println("共执行了 " + numberOfTasks.get() + " 个任务");
    }

    // 自定义线程工厂
    static class CustomThreadFactory implements ThreadFactory {
        private final String namePrefix = "AdvancedThreadPool-Thread-";
        private final AtomicInteger threadIndex = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(namePrefix + threadIndex.getAndIncrement());
            thread.setDaemon(false); // 设置为非守护线程
            return thread;
        }
    }
}
