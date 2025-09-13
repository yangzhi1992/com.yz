package com.commons.timer.spring.spring2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.concurrent.ScheduledFuture;

@Component
public class MyTaskScheduler {

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler2;

    private ScheduledFuture<?> scheduledFuture;

    // 动态调度任务（按固定频率执行任务）
    public void scheduleTaskWithFixedRate() {
        scheduledFuture = taskScheduler2.scheduleAtFixedRate(() -> {
            System.out.println("Fixed rate task executed at: " + LocalTime.now());
        }, 5000); // 每 5 秒执行一次
    }

    //
    public void scheduleWithFixedDelay() {
        scheduledFuture = taskScheduler2.scheduleWithFixedDelay(() -> {
            System.out.println("Fixed rate task executed at: " + LocalTime.now());
        }, 5000); // 延迟 5 秒执行一次
    }

    // 动态调度任务（按 Cron 表达式执行任务）
    public void scheduleTaskWithCron(String cronExpression) {
        scheduledFuture = taskScheduler2.schedule(() -> {
            System.out.println("Cron task executed at: " + LocalTime.now());
        }, new CronTrigger(cronExpression));
    }

    // 停止任务
    public void stopTask() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            System.out.println("Task stopped");
        }
    }
}
