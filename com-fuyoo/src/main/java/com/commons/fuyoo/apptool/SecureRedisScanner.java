package com.commons.fuyoo.apptool;

import com.google.common.util.concurrent.RateLimiter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class SecureRedisScanner {
	// QPS统计
	private static final AtomicLong totalProcessed = new AtomicLong(0);
	private static final AtomicLong totalDeleted = new AtomicLong(0);
	private static volatile long lastQpsTime = System.currentTimeMillis();
	private static volatile long lastProcessedCount = 0;

	// 线程池配置
	private static final int THREAD_POOL_SIZE = 10; // 线程池大小
	private static final int BATCH_SIZE = 100; // 每批处理的key数量

	// RateLimiter配置 - 限制处理速度为1500 QPS
	private static final double MAX_QPS = 1500.0;
	private static RateLimiter rateLimiter;

	// 文件配置
	private static final String OUTPUT_FILE = "D:\\beifen\\RMC_10000.txt";

	public static void main(String[] args) {
		// Redis连接配置
		String host = "****";
		int port = 7300;
		String password = "****";
		int timeout = 2000;

		// 扫描配置
		String keyPattern = "test*";
		int scanBatchSize = 1000;

		// 初始化RateLimiter
		rateLimiter = RateLimiter.create(MAX_QPS);
		System.out.println("RateLimiter已初始化，限制处理速度为: " + MAX_QPS + " QPS");

		// 配置连接池 - 增加连接数以支持多线程
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(THREAD_POOL_SIZE * 2);  // 最大连接数
		poolConfig.setMaxIdle(THREAD_POOL_SIZE);       // 最大空闲连接
		poolConfig.setMinIdle(5);                      // 最小空闲连接
		poolConfig.setTestOnBorrow(true);              // 获取连接时测试
		poolConfig.setTestWhileIdle(true);             // 空闲时测试连接

		// 创建连接池
		try (JedisPool jedisPool = new JedisPool(
				poolConfig,
				host,
				port,
				timeout,
				password   // 密码
		)) {
			System.out.println("成功连接到Redis服务器");
			System.out.println("输出文件: " + OUTPUT_FILE);
			System.out.println("线程池大小: " + THREAD_POOL_SIZE);
			System.out.println("批次大小: " + BATCH_SIZE);
			System.out.println("开始处理...\n");

			// 启动QPS监控线程
			ScheduledExecutorService qpsMonitor = Executors.newScheduledThreadPool(1);
			qpsMonitor.scheduleAtFixedRate(() -> printQps(), 1, 1, TimeUnit.SECONDS);

			// 执行SCAN操作并处理
			scanKeysWithAuth(jedisPool, keyPattern, scanBatchSize);

			// 停止QPS监控
			qpsMonitor.shutdown();

			// 等待所有任务完成
			System.out.println("\n等待所有任务完成...");
			Thread.sleep(2000);

		} catch (Exception e) {
			System.err.println("Redis连接或操作失败: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void scanKeysWithAuth(JedisPool jedisPool, String pattern, int batchSize) {
		String cursor = "0";
		ScanParams scanParams = new ScanParams().match(pattern)
				.count(batchSize);
		long totalScanned = 0;
		long startTime = System.currentTimeMillis();

		System.out.println("开始扫描匹配 " + pattern + " 的keys...");

		// 创建线程池
		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

		// 创建文件写入器
		try (BufferedWriter writer = new BufferedWriter(
				new FileWriter(OUTPUT_FILE, true))) {

			// 用于批量收集keys
			List<String> keyBatch = new ArrayList<>();
			final int SCAN_BATCH_THRESHOLD = BATCH_SIZE * 2; // 扫描批次阈值

			do {
				// 执行SCAN命令
				try (Jedis jedis = jedisPool.getResource()) {
					ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
					cursor = scanResult.getCursor();

					// 处理结果
					List<String> keys = scanResult.getResult();
					if (!keys.isEmpty()) {
						totalScanned += keys.size();
						keyBatch.addAll(keys);

						// 当累积到一定数量时，提交批处理任务
						if (keyBatch.size() >= SCAN_BATCH_THRESHOLD) {
							List<String> batchToProcess = new ArrayList<>(keyBatch);
							keyBatch.clear();

							executorService.submit(() ->
									processBatch(jedisPool, batchToProcess, writer));
						}
					}
				} catch (Exception e) {
					System.err.println("扫描过程中出错: " + e.getMessage());
				}

				// 当cursor返回为0时表示扫描完成
			} while (!"0".equals(cursor));

			// 处理剩余的keys
			if (!keyBatch.isEmpty()) {
				executorService.submit(() ->
						processBatch(jedisPool, keyBatch, writer));
			}

			// 关闭线程池并等待所有任务完成
			executorService.shutdown();
			try {
				if (!executorService.awaitTermination(60, TimeUnit.MINUTES)) {
					executorService.shutdownNow();
				}
			} catch (InterruptedException e) {
				executorService.shutdownNow();
				Thread.currentThread()
						.interrupt();
			}

			long duration = System.currentTimeMillis() - startTime;
			System.out.printf("\n扫描完成! 共扫描 %d 个key, 处理 %d 个key, 删除 %d 个key, 总耗时 %d ms\n",
					totalScanned, totalProcessed.get(), totalDeleted.get(), duration);

		} catch (IOException e) {
			System.err.println("文件写入失败: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void processBatch(JedisPool jedisPool, List<String> keys, BufferedWriter writer) {
		if (keys.isEmpty()) {
			return;
		}

		try (Jedis jedis = jedisPool.getResource()) {
			// 分批处理，避免单次操作过多
			for (int i = 0; i < keys.size(); i += BATCH_SIZE) {
				int end = Math.min(i + BATCH_SIZE, keys.size());
				List<String> batch = keys.subList(i, end);

				// 使用RateLimiter限制处理速度
				// 获取许可，每个key需要1个许可
				int permits = batch.size();
				rateLimiter.acquire(permits);

				// 批量获取values并写入文件
				List<String> values = batchGetValues(jedis, batch);
				writeToFile(writer, batch, values);

				// 批量删除keys
				batchDeleteKeys(jedis, batch);

				// 更新统计
				totalProcessed.addAndGet(batch.size());
				totalDeleted.addAndGet(batch.size());
			}
		} catch (Exception e) {
			System.err.println("批处理过程中出错: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static List<String> batchGetValues(Jedis jedis, List<String> keys) {
		List<String> values = new ArrayList<>();

		// 使用Pipeline批量获取
		Pipeline pipeline = jedis.pipelined();
		for (String key : keys) {
			pipeline.lrange(key, 0, -1);
		}
		List<Object> results = pipeline.syncAndReturnAll();

		for (Object result : results) {
			if (result != null) {
				values.add(result.toString());
			} else {
				values.add("(null)");
			}
		}

		return values;
	}

	private static void writeToFile(BufferedWriter writer, List<String> keys, List<String> values) {
		synchronized (writer) {
			try {
				for (int i = 0; i < keys.size(); i++) {
					writer.write(keys.get(i) + "|" +
							(i < values.size() ? values.get(i) : "(null)"));
					writer.newLine();
				}
				writer.flush(); // 及时刷新到磁盘
			} catch (IOException e) {
				System.err.println("写入文件失败: " + e.getMessage());
			}
		}
	}

	private static void batchDeleteKeys(Jedis jedis, List<String> keys) {
		// 使用Pipeline批量删除
		Pipeline pipeline = jedis.pipelined();
		for (String key : keys) {
			pipeline.del(key);
		}
		pipeline.sync();
	}

	private static void printQps() {
		long currentTime = System.currentTimeMillis();
		long currentProcessed = totalProcessed.get();
		long currentDeleted = totalDeleted.get();

		long timeDelta = currentTime - lastQpsTime;
		if (timeDelta > 0) {
			long processedDelta = currentProcessed - lastProcessedCount;
			double qps = (processedDelta * 1000.0) / timeDelta;

			// 显示RateLimiter的当前速率
			double availablePermits = rateLimiter.getRate();

			System.out.printf("[QPS监控] 处理QPS: %.2f, 限制速率: %.2f, 总处理: %d, 总删除: %d\n",
					qps, availablePermits, currentProcessed, currentDeleted);
		}

		lastQpsTime = currentTime;
		lastProcessedCount = currentProcessed;
	}
}
