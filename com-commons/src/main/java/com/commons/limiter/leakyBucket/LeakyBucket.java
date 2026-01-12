package com.commons.limiter.leakyBucket;

import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class LeakyBucket {

	private static Logger logger = LoggerFactory.getLogger(LeakyBucket.class);

	private LinkedBlockingQueue<Item> queue;

	private ScheduledFuture<?> future;

	public static String LeakyBucketDefault = "LBDF";

	//时间间隔
	private static int interval = 1;
	//时间间隔范围内的消费速度
	private static int rate = 20;
	//消息的有效期
	private static long maxWatingTime = 60000;
	//队列满了是否丢弃消息
	private static boolean discardWhenFull = false;
	//初始化消费者的大小
	private static int schedulePoolSize = 2;
	//queue size
	private static int queueSize = 10000;
	//消费者线程池
	private ScheduledExecutorService executor;

	private static Map<String, LeakyBucket> leakyBucketMap = new HashMap<String, LeakyBucket>();

	static {
		leakyBucketMap.put(LeakyBucketDefault,
				new LeakyBucket(rate, maxWatingTime, schedulePoolSize, queueSize, discardWhenFull));
	}

	public static LeakyBucket getDefaultInstance() {
		return leakyBucketMap.get(LeakyBucketDefault);
	}

	public static LeakyBucket getInstance(String name, int rate, long maxWatingTime, int schedulePoolSize, int queueSize, boolean discardWhenFull) {
		//获取对应的漏桶对象，如果找不到则使用默认的
		LeakyBucket leakyBucket = leakyBucketMap.get(name);
		if (leakyBucket == null) {
			leakyBucket = getDefaultInstance();
		}

		//如果限流信息有变化则重新生成一个
		if (leakyBucket.rate != rate || leakyBucket.maxWatingTime != maxWatingTime
				|| leakyBucket.schedulePoolSize != schedulePoolSize || leakyBucket.queueSize != queueSize
				|| leakyBucket.discardWhenFull != discardWhenFull) {
			logger.info("refresh leaky bucket {}!={}||{}!={}||{}!={}||{}!={}||{}!={}",
					new Object[] {LeakyBucket.rate, rate, LeakyBucket.maxWatingTime, maxWatingTime,
							LeakyBucket.schedulePoolSize, schedulePoolSize, LeakyBucket.queueSize, queueSize,
							LeakyBucket.discardWhenFull, discardWhenFull});
			leakyBucket = new LeakyBucket(rate, maxWatingTime, schedulePoolSize, queueSize, discardWhenFull);
			//更新到map里面
			leakyBucketMap.put(name, leakyBucket);
			logger.info("new leaky bucket {}!={}||{}!={}||{}!={}||{}!={}||{}!={}",
					new Object[] {LeakyBucket.rate, rate, LeakyBucket.maxWatingTime, maxWatingTime,
							LeakyBucket.schedulePoolSize, schedulePoolSize, LeakyBucket.queueSize, queueSize,
							LeakyBucket.discardWhenFull, discardWhenFull});

		}

		return leakyBucket;
	}

	private LeakyBucket(int rate, long maxWatingTime, int schedulePoolSize, int queueSize, boolean discardWhenFull) {
		super();
		LeakyBucket.rate = rate;
		LeakyBucket.maxWatingTime = maxWatingTime;
		LeakyBucket.schedulePoolSize = schedulePoolSize;
		LeakyBucket.queueSize = queueSize;
		LeakyBucket.discardWhenFull = discardWhenFull;
		this.executor = Executors.newScheduledThreadPool(LeakyBucket.schedulePoolSize);
		this.queue = new LinkedBlockingQueue<Item>(queueSize);
		refreshRate();
	}

	public void submitTask(BucketEvent event) {
		if (LeakyBucket.discardWhenFull && this.queue.remainingCapacity() < 1) {
			logger.error("leakyBucket is full {} {}", this.queue.remainingCapacity(), event);
		} else {
			this.queue.offer(new Item(event, System.currentTimeMillis()));
		}

	}

	public void refreshRate() {
		//取消定时执行的线程
		if (this.future != null && !this.future.isCancelled()) {
			this.future.cancel(false);
		}
		//定时执行的时间间隔
		long inter = LeakyBucket.interval * 1000 * 20 / LeakyBucket.rate;
		//重新生成定时任务
		this.future = this.executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				consumeItem();
			}

		}, 0, inter, TimeUnit.MILLISECONDS);
	}

	//消费消息
	private void consumeItem() {
		while (!queue.isEmpty()) {
			Item item = queue.poll();
			if (item != null) {
				//计算等待时间
				long delta = System.currentTimeMillis() - item.getTimestamp();
				//有效期内进行处理
				if (delta <= maxWatingTime) {
					item.getEvent()
							.finishEvent();
					break;
				} else {
					logger.warn("msg expired {} {}", delta, JSON.toJSONString(item));
				}
			} else {
				break;
			}
		}
	}

	public static void main(String args[]) throws InterruptedException {
		int rate = 10;
		LeakyBucket bucket = new LeakyBucket(rate, 1000, 1, 100, false);

		int size = 100;

		for (int i = 0; i < size; i++) {
			final int TaskID = i;
			bucket.submitTask(new BucketEvent() {
				@Override
				public void finishEvent() {
					System.out.println("task id :" + TaskID);

				}
			});
		}
		Thread.sleep(size * rate);
		System.out.println("end");

	}
}
