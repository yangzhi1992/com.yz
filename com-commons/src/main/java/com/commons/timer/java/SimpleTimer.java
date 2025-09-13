package com.commons.timer.java;

import java.util.Date;

// 简单的循环定时任务 Thread.sleep() + 循环
public class SimpleTimer {
    public static void main(String[] args) {
        Runnable task = () -> {
            while (true) {
                try {
                    // 执行任务
                    System.out.println("Task executed at: " + new Date());
                    // 休眠1秒
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        
        new Thread(task).start();
    }
}