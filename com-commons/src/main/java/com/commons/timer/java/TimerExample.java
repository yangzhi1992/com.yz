package com.commons.timer.java;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

/**
 * 单线程执行：所有任务都在同一个线程中顺序执行，如果一个任务执行时间过长，会影响其他任务的执行
 * 异常处理：如果任务抛出未捕获的异常，Timer 线程会终止，所有后续任务都不会执行
 * 缺乏灵活性：不支持 cron 表达式等复杂调度需求
 * 精度问题：依赖于系统时钟，精度有限
 */
public class TimerExample {
    public static void main(String[] args) {
        Timer timer = new Timer();
        
        // 一次性任务
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("One-time task executed at: " + new Date());
            }
        }, 2000);
        
        // 固定延迟重复任务
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Fixed-delay task executed at: " + new Date());
                try {
                    // 模拟任务执行时间
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1000, 2000);
        
        // 固定速率重复任务
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Fixed-rate task executed at: " + new Date());
                try {
                    // 模拟任务执行时间
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1000, 2000);
        
        // 运行一段时间后停止所有任务
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Cancelling all tasks...");
                timer.cancel();
            }
        }, 10000);
    }
}