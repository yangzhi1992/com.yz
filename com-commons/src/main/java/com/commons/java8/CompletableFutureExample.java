package com.commons.java8;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class CompletableFutureExample {
	public static void main(String[] args) {
		// 创建一个无返回值的异步任务
		CompletableFuture<Void> noReturnTask = CompletableFuture.runAsync(() -> {
			System.out.println("Running asynchronous task");
		});

		// 创建一个有返回值的异步任务
		CompletableFuture<String> returnTask = CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "Task result";
		});
		// 获取结果
		System.out.println(returnTask.join()); // 输出 "Task result"

		// 串行调用
		CompletableFuture.supplyAsync(() -> {
			return "Hello";
		}).thenApply(result -> {   //将结果应用函数转换为另一个结果
			return result + ", CompletableFuture";
		}).thenAccept(result -> {  //消费结果，没有返回值
			System.out.println("Processed Result: " + result);
		});

		// 异步回调
		CompletableFuture.supplyAsync(() -> {
			return "Hello";
		}).thenApplyAsync(result -> {
			return result + " from CompletableFuture!";
		}).thenAccept(System.out::println);


		// 两个任务的组合
		CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 10);
		CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 20);

		future1.thenCombine(future2, (x, y) -> x + y)
				.thenAccept(result -> System.out.println("Combined Result: " + result));


		// 多个任务的组合 allOf,anyOf
		CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
			return "Task 1 Completed";
		});
		CompletableFuture<String> future4 = CompletableFuture.supplyAsync(() -> {
			return "Task 2 Completed";
		});
		CompletableFuture.allOf(future1, future2).thenRun(() -> {
			System.out.println("All tasks finished");
		});
		CompletableFuture<Object> anyResult = CompletableFuture.anyOf(future1, future2);
		anyResult.thenAccept(result -> System.out.println("First Completed: " + result));
	}
}
