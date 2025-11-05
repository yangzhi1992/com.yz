package com.commons.timer.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class ScheduledThreadPoolHighExample {
	public static void main(String[] args) throws InterruptedException {
		//1. 自定义线程工厂方式一 : 将自定义的线程工厂作为第二个参数传入
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2,
				r -> {
					Thread thread = new Thread(r);
					thread.setName("CustomSchedulerThread-" + System.currentTimeMillis()); //设置线程名称
					thread.setDaemon(true); // 设置为守护线程
					return thread;
				});

		//2. 自定义线程工厂方式二 : 通过创建BasicThreadFactory实现第二个参数
		ScheduledExecutorService executorFactory = Executors.newScheduledThreadPool(2,
				new BasicThreadFactory.Builder().namingPattern("CustomSchedulerThread-%d")
						.daemon(true)
						.build()
		);

		//3. 自定义线程工厂方式三 : 通过创建CustomThreadFactory实现第二个参数
		ScheduledExecutorService executorFactory2 = Executors.newScheduledThreadPool(5, new CustomThreadFactory());

		//2. 异常处理
		executor.schedule(() -> {
			try {
				// 可能抛出异常的任务
				//riskyTask();
			} catch (Exception e) {
				System.err.println("任务执行失败: " + e.getMessage());
				// 异常不会影响其他任务的执行
			}
		}, 1, TimeUnit.SECONDS);

		//3. 动态调整核心线程数
		ScheduledThreadPoolExecutor scheduledExecutor = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(2);
		// 修改核心线程数
		scheduledExecutor.setCorePoolSize(4);
		// 修改最大线程池数
		scheduledExecutor.setMaximumPoolSize(4);
		// 修改拒绝策略
		scheduledExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		// 允许核心线程超时退出（默认情况下核心线程不会超时退出-超时时间是60s）
		scheduledExecutor.setKeepAliveTime(60, TimeUnit.SECONDS);
		scheduledExecutor.allowCoreThreadTimeOut(true);

		//停止接收新任务，等待已提交任务完成
		executor.shutdown();
		try {
			//阻塞当前线程，最多等待 60 秒，让线程池中已提交的任务完成;如果在 60 秒内线程池已经终止，则返回 true；否则返回 false。
			if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
				// 立即关闭：强制停止线程池中所有已经启动但未完成的线程任务，并清空等待队列中的任务。
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			//捕获 InterruptedException 并恢复中断
			//  awaitTermination 方法可能会因为线程被中断抛出 InterruptedException。
			//  捕捉异常后，一般会：
			//      调用 shutdownNow() 强制关闭线程池。
			//      恢复线程的中断状态：Thread.currentThread().interrupt()，用于告知上层调用代码该线程被中断。
			executor.shutdownNow();
			Thread.currentThread()
					.interrupt();
		}
	}

	// 自定义线程工厂
	static class CustomThreadFactory implements ThreadFactory {
		private final String namePrefix = "MyThreadPool-Thread-";
		private final AtomicInteger threadIndex = new AtomicInteger(1); // 用于线程的编号
		private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> {
			// 捕获未处理的异常
			System.err.println("异常被捕获在线程: " + t.getName() + ", 错误信息: " + e.getMessage());
		};

		@Override
		public Thread newThread(Runnable r) {
			// 创建线程并自定义逻辑
			Thread thread = new Thread(r);
			thread.setName(namePrefix + threadIndex.getAndIncrement());
			thread.setDaemon(false); // 设置非守护线程，程序会等到所有线程完成才会终止
			thread.setUncaughtExceptionHandler(uncaughtExceptionHandler); // 设置异常捕获
			System.out.println("线程已创建：" + thread.getName()); // 打印线程创建日志
			return thread;
		}
	}
}