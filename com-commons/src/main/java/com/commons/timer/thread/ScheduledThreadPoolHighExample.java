package com.commons.timer.thread;

import java.util.concurrent.*;

public class  ScheduledThreadPoolHighExample {
    public static void main(String[] args) throws InterruptedException {
        //1. 自定义线程工厂
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2, r -> {
            Thread thread = new Thread(r);
            thread.setName("CustomSchedulerThread-" + System.currentTimeMillis()); //设置线程名称
            thread.setDaemon(true); // 设置为守护线程
            return thread;
        });

        //2. 异常处理
        executor.schedule(() -> {
            try {
                // 可能抛出异常的任务
                //riskyTask();
            } catch (Exception e) {
                System.err.println("任务执行失败: " + e.getMessage());
                // 异常不会影响其他任务的执行
            }
        }, 1, TimeUnit.SECONDS);

        //3. 动态调整核心线程数
        ScheduledThreadPoolExecutor scheduledExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(2);
        // 修改核心线程数
        scheduledExecutor.setCorePoolSize(4);
        // 修改最大线程池数
        scheduledExecutor.setMaximumPoolSize(4);
        // 修改拒绝策略
        scheduledExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 允许核心线程超时退出（默认情况下核心线程不会超时退出-超时时间是60s）
        scheduledExecutor.setKeepAliveTime(60, TimeUnit.SECONDS);
        scheduledExecutor.allowCoreThreadTimeOut(true);

        //停止接收新任务，等待已提交任务完成
        executor.shutdown();
        try {
            //阻塞当前线程，最多等待 60 秒，让线程池中已提交的任务完成;如果在 60 秒内线程池已经终止，则返回 true；否则返回 false。
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                // 立即关闭：强制停止线程池中所有已经启动但未完成的线程任务，并清空等待队列中的任务。
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            //捕获 InterruptedException 并恢复中断
            //  awaitTermination 方法可能会因为线程被中断抛出 InterruptedException。
            //  捕捉异常后，一般会：
            //      调用 shutdownNow() 强制关闭线程池。
            //      恢复线程的中断状态：Thread.currentThread().interrupt()，用于告知上层调用代码该线程被中断。
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}