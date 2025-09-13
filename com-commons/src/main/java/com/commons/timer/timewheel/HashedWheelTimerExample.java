package com.commons.timer.timewheel;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

public class HashedWheelTimerExample {

    public static void main(String[] args) {
        // 创建一个 HashedWheelTimer 定时器
        HashedWheelTimer timer = new HashedWheelTimer(
                100,               // 时间轮刻度（粒度）大小，单位为 ms
                TimeUnit.MILLISECONDS,        // 时间粒度单位
                256                           // 时间轮的槽数（建议是 2 的幂次）
        );

        // 提交一个延迟任务，延迟 3 秒后执行任务
        timer.newTimeout(
                new TimerTask() {
                    @Override
                    public void run(Timeout timeout) throws Exception {
                        System.out.println("Task executed: " + System.currentTimeMillis());
                    }
                },
                3,  // 延迟时间（3 秒）
                TimeUnit.SECONDS
        );

        // 提交另一个任务，延迟 2 秒后触发
        timer.newTimeout(
                timeout -> System.out.println("Another task executed: " + System.currentTimeMillis()),
                2,
                TimeUnit.SECONDS
        );

        // 主线程等待：为了观察任务结果，不让 JVM 退出
        try {
            Thread.sleep(5000); // 等待 5 秒，确保所有延迟的任务执行
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 停止定时器（释放线程资源）
        timer.stop();
    }
}
