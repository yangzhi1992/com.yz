package com.commons.thread.daemon;

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

        Thread t = new Thread() {

            @Override
            public void run() {
                Thread innerThread = new Thread() {

                    @Override
                    public void run() {
                        try {
                            while(true) {
                                System.out.println("Do something for health check.");
                                Thread.sleep(10000);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                };

                innerThread.setDaemon(true);
                innerThread.start();

                try {
                    Thread.sleep(1000);
                    System.out.println("T thread finish done.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        };
        t.start();

    }
}
