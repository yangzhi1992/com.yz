package com.commons.thread.书籍.并发编程实战;

import java.util.concurrent.CountDownLatch;

/**
 * TestHarness
 * <p/>
 * Using CountDownLatch for starting and stopping threads in timing tests
 *
 * @author Brian Goetz and Tim Peierls
 */
public class TestHarness {
	public static void main(String[] args) throws InterruptedException {
		TestHarness th = new TestHarness();
		long time = th.timeTasks(2, new Runnable(){
			@Override
			public void run() {
				System.out.println("呵呵");
			}
		});
		System.out.println("result:"+time);
	}
    public long timeTasks(int nThreads, final Runnable task)throws InterruptedException {
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            Thread t = new Thread() {
                public void run() {
                    try {
                        startGate.await();
                        try {
                            task.run();
                        } finally {
                            endGate.countDown();
                        }
                    } catch (InterruptedException ignored) {
                    }
                }
            };
            t.start();
        }

        long start = System.nanoTime();
        System.out.println("start:"+start);
        startGate.countDown();
        endGate.await();
        long end = System.nanoTime();
        System.out.println("end:"+start);
        return end - start;
    }
}
