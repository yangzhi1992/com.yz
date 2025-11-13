package com.commons.javase.collectionframework;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class ExQueueExample {
	public static void main(String[] args) throws InterruptedException {
		priorityQueueTest();
		linkedListTest();
		arrayBlockingQueueTest();
		linkedBlockingQueueTest();
		priorityBlockingQueueTest();
		delayQueueTest();
		synchronousQueueTest();
	}

	public static void priorityQueueTest() {
		PriorityQueue<Integer> pq = new PriorityQueue<>();
		pq.add(30);
		pq.add(20);
		pq.add(40);
		pq.add(10);

		while (!pq.isEmpty()) {
			System.out.println(pq.poll()); // 按优先级升序输出：10, 20, 30, 40
		}
	}
	public static void linkedListTest() {
		Queue<String> queue = new LinkedList<>();

		queue.offer("A"); // 插入队列
		queue.offer("B");
		queue.offer("C");

		System.out.println(queue.poll()); // A
		System.out.println(queue.poll()); // B
	}

	public static void arrayBlockingQueueTest() {
		// 创建一个容量为 3 的阻塞队列
		ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(3);

		try {
			queue.put("Task1");
			queue.put("Task2");
			queue.put("Task3");

			System.out.println(queue.take()); // 输出 Task1
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void linkedBlockingQueueTest() {
		LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

		// 多线程使用
		Thread producer = new Thread(() -> {
			try {
				for (int i = 1; i <= 5; i++) {
					queue.put(i);
					System.out.println("Produced: " + i);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		Thread consumer = new Thread(() -> {
			try {
				while (true) {
					Integer item = queue.take();
					System.out.println("Consumed: " + item);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		producer.start();
		consumer.start();
	}

	public static  void priorityBlockingQueueTest() {
		PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>();

		queue.put(50);
		queue.put(30);
		queue.put(10);

		while (!queue.isEmpty()) {
			System.out.println(queue.poll()); // 输出：10, 30, 50
		}
	}

	public static  void delayQueueTest() throws InterruptedException {
		DelayQueue<DelayedTask> queue = new DelayQueue<>();

		queue.put(new DelayedTask("Task1", 1000));
		queue.put(new DelayedTask("Task2", 2000));

		System.out.println(queue.take()); // 延迟 1 秒后输出 Task1
		System.out.println(queue.take()); // 延迟 2 秒后输出 Task2
	}

	public static  void synchronousQueueTest() throws InterruptedException {
		SynchronousQueue<String> queue = new SynchronousQueue<>();

		// 生产者
		Thread producer = new Thread(() -> {
			try {
				queue.put("Produced Item");
				System.out.println("Item produced!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		// 消费者
		Thread consumer = new Thread(() -> {
			try {
				String item = queue.take();
				System.out.println("Item consumed: " + item);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		producer.start();
		consumer.start();
	}

	public static class DelayedTask implements Delayed {
		private final String name;
		private final long startTime;

		public DelayedTask(String name, long delayInMilliseconds) {
			this.name = name;
			this.startTime = System.currentTimeMillis() + delayInMilliseconds;
		}

		@Override
		public long getDelay(TimeUnit unit) {
			long difference = startTime - System.currentTimeMillis();
			return unit.convert(difference, TimeUnit.MILLISECONDS);
		}

		@Override
		public int compareTo(Delayed o) {
			return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
		}

		@Override
		public String toString() {
			return "Task name: " + name;
		}
	}
}
