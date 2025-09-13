package com.commons.timer.spring.spring1;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@EnableScheduling
public class SpringScheduler {
    
    // 每5秒执行一次 间隔时间（以毫秒为单位），不论前一个任务是否完成，都会定时开始新的任务。
    @Scheduled(fixedRate = 5000)
    public void fixedRateTask() {
        System.out.println("Fixed rate task executed at: " + new Date());
    }
    
    // 使用cron表达式，每分钟的第30秒执行 ： 使用 Cron 表达式定义复杂的任务调度规则。
    @Scheduled(cron = "30 * * * * ?")
    public void cronTask() {
        System.out.println("Cron task executed at: " + new Date());
    }
    
    // 固定延迟，上次任务完成后延迟3秒执行 ： 上一个任务完成后的延迟时间（以毫秒为单位）。
    @Scheduled(fixedDelay = 3000)
    public void fixedDelayTask() {
        System.out.println("Fixed delay task executed at: " + new Date());
    }

}