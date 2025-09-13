package com.commons.timer.spring.spring2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class SchedulerConfig {

    /**
     * TaskScheduler 默认实现类为 ThreadPoolTaskScheduler，可以注册为一个 Bean：
     * 设置为 CPU 核心数的 2 倍，用于 I/O 密集任务。
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler2() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);                                                           // 设置线程池的线程数量
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());   //设置拒绝策略
        scheduler.setThreadNamePrefix("MyScheduledTask-");                                  // 设置线程名前缀
        scheduler.setRemoveOnCancelPolicy(true);                                            // 任务取消后从队列中移除
        scheduler.setWaitForTasksToCompleteOnShutdown(true);                                // 优雅停机
        scheduler.setAwaitTerminationSeconds(60);                                           // 等待线程任务完成的最大时间
        return scheduler;
    }


}
