package com.commons.thread.threadpools;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolExample {
	public static void main(String[] args) {
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
	}
}
