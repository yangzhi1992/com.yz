package com.commons.thread.threadpools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExample {
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		//创建一个线程数量固定、线程数量不会变化的线程池，当线程被占用时，任务会存放到队列中等待处理。
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(10);

		//如果线程池中存在空的线程可用，则会复用空闲线程来处理任务,如果线程池中没有可用线程，则会创建一个新的线程
		//SynchronousQueue 作为任务队列
		//SynchronousQueue 是没有任何内部缓冲的队列，没有容量
		//每个插入操作需要等待另一个线程的移除操作，否则会阻塞
		//每次只能传递一个元素
		//如果没有其他线程接收，插入线程会被阻塞
		ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

		//创建一个线程数固定为 1 的线程池，任务会按添加顺序对线程进行排队执行
		ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();

		//创建一个定时调度线程池
		ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(2);
		newScheduledThreadPool.schedule(() -> System.out.println("Task executed after 2 seconds"), 2, TimeUnit.SECONDS);

		//1、ThreadPoolExecutor execute,submit方法使用
		//2、继承ThreadPoolExecutor类重写beforeExecute，afterExecute，terminated方法
		//3、Future的get()方法具有阻塞性，等异步执行完后返回结果后才会执行get()方法后面的内容
		TimingThreadPool timingThreadPool = new TimingThreadPool();
		List<Future<Integer>> futures = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			//execute 返回void
			timingThreadPool.execute(() -> {
				System.out.println(Thread.currentThread()
						.getName());
			});

			//submit 返回参数Future<?> , 入参runnable时 ？是void
			int finalI = i;
			Future<?> runableFuture = timingThreadPool.submit(() -> {
				System.out.println("ss" + finalI);
			});
			System.out.println(runableFuture.get());

			//submit 返回参数Future<?> , 入参callable时 ？是返回值Integer
			Future<Integer> callableFuture = timingThreadPool.submit(() -> {
				System.out.println("Performing a time-consuming calculation...");
				Thread.sleep(10000); // 模拟业务逻辑处理
				return 42; // 返回实际结果
			});
			futures.add(callableFuture);
		}

		futures.forEach(v -> {
			try {
				System.out.println(v.get());
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		});

		System.out.println("end");
	}
}
