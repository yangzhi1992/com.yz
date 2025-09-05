package com.commons.thread.并发工具类.闭锁.countdownlatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * TestHarness
 * <p/>
 * Using CountDownLatch for starting and stopping threads in timing tests
 *
 * @author Brian Goetz and Tim Peierls
 */
public class CountDownLatchTest3 {
	public static void main(String[] args) throws InterruptedException {
		CountDownLatchTest3 th = new CountDownLatchTest3();
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
        cdl.await(3, TimeUnit.SECONDS); //阻塞线程继续执行，等到3秒后阻塞变为非阻塞（cdl计数如果一直未为0）
        System.out.println("end");
    }
}
