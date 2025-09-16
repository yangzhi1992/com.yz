package com.commons.thread;

public class DaemonThreadExample {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000);
                System.out.println("异步线程任务完成");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.setDaemon(true); // 设置为守护线程
        thread.start();
        System.out.println("main 方法结束");
    }
}
