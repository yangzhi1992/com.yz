package com.commons.thread.daemon;

public class DaemonThread {

    public static void main(String[] args) throws InterruptedException {
        
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName() + " running");
                    Thread.sleep(10000);
                    System.out.println(Thread.currentThread().getName() + " done");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };

        t.setDaemon(true);
        // runnable -> running|dead|blocked
        t.start();

        Thread.sleep(5000);
        System.out.println(Thread.currentThread().getName());

    }
    
}