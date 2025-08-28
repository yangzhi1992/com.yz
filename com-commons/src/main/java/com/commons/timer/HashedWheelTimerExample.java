package com.commons.timer;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.TimeUnit;

public class HashedWheelTimerExample {
    
    public static void main(String[] args) throws InterruptedException {
        // 创建定时器
        HashedWheelTimer timer = new HashedWheelTimer(
            new DefaultThreadFactory("demo-timer"),
            100, TimeUnit.MILLISECONDS, 512);
        
        System.out.println("Start time: " + System.currentTimeMillis());
        
        // 提交多个定时任务
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            timer.newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    System.out.println("Task " + taskId + " executed at: " + System.currentTimeMillis());
                }
            }, i * 500, TimeUnit.MILLISECONDS); // 每个任务间隔500毫秒
        }
        
        // 等待所有任务执行完成
        Thread.sleep(6000);
        
        // 停止定时器
        timer.stop();
    }
}