package com.commons.thread.futuretask;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class FutureTaskExample {
	public static void main(String[] args) {
		threadPoolsFutureTask();
		threadFutureTask();
	}

	public static void threadPoolsFutureTask() {
		// 创建线程池
		ExecutorService executor = Executors.newFixedThreadPool(2);

		// 创建 Callable 并封装为 FutureTask
		Callable<String> callableTask = () -> {
			Thread.sleep(3000);
			return "Task is completed!";
		};

		FutureTask<String> futureTask = new FutureTask<>(callableTask);

		// 将 FutureTask 提交给线程池
		executor.submit(futureTask);

		try {
			System.out.println("Main thread is doing other work...");
			String result = futureTask.get(); // 阻塞等待，获取结果
			System.out.println("Task result: " + result);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}
	}

	public static void threadFutureTask() {
		// 创建 Callable 任务
		Callable<Integer> callableTask = () -> {
			System.out.println("Task is running...");
			Thread.sleep(2000); // 模拟任务耗时
			return 42; // 任务返回值
		};

		// 使用 FutureTask 包装 Callable
		FutureTask<Integer> futureTask = new FutureTask<>(callableTask);

		// 使用单独线程执行任务
		Thread thread = new Thread(futureTask);
		thread.start();

		try {
			System.out.println("Waiting for task to complete...");
			Integer result = futureTask.get(); // 阻塞，等待结果
			System.out.println("Task completed! Result: " + result);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}
