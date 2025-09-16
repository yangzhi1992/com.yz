package com.commons.timer.spring.spring1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync      // 启用异步
@EnableScheduling // 启用定时任务
public class SpringScheduler2 {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);   // 核心线程数
        executor.setMaxPoolSize(10);   // 最大线程数
        executor.setQueueCapacity(25); // 队列容量
        executor.setKeepAliveSeconds(60); //设置有效时间
        executor.setThreadNamePrefix("AsyncTask-"); // 线程名前缀
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); //设置拒绝策略
        executor.initialize();
        return executor;
    }

    // 使用自定义线程池
    @Async("taskExecutor")
    // 每5秒执行一次 间隔时间（以毫秒为单位），不论前一个任务是否完成，都会定时开始新的任务。
    @Scheduled(fixedRate = 5000)
    public void fixedRateTask2() {
        System.out.println("Fixed rate task executed at: " + new Date());
    }

}