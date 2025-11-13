package com.commons.thread.daemon;

public class AsyncThreadExample {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000); // 模拟耗时操作
                System.out.println("异步线程任务完成");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start(); // 启动异步线程
        System.out.println("main 方法结束");
    }
}
