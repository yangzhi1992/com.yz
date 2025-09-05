package com.commons.thread.daemon;

public class DaemonThread1 {

    public static void main(String[] args)  {
        
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

        // runnable -> running|dead|blocked
        t.start();
    }
    
}