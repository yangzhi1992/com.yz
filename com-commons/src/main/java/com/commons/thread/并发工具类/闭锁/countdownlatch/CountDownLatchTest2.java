package com.commons.thread.并发工具类.闭锁.countdownlatch;

import java.util.concurrent.CountDownLatch;

/**
 * TestHarness
 * <p/>
 * Using CountDownLatch for starting and stopping threads in timing tests
 *
 * @author Brian Goetz and Tim Peierls
 */
public class CountDownLatchTest2 {
	public static void main(String[] args) throws InterruptedException {
		CountDownLatchTest2 th = new CountDownLatchTest2();
		th.timeTasks();
	}
    public void timeTasks()throws InterruptedException {
        final CountDownLatch cdl = new CountDownLatch(1);

        Thread t = new Thread() {
            public void run() {
                while(true){
                	try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                	System.out.println("a");
                	//cdl.countDown();
                }
            }
        };
        t.start();
        cdl.await(); //阻塞线程继续执行，等到cdl的计数为0时阻塞停止
        System.out.println("end");
    }
}
