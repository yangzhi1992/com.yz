package com.commons.timer.spring.spring3;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 在你想异步执行的方法上加上 @Async 注解，Spring 会帮你将该方法放入线程池中运行，方法调用会立即返回，而任务会在后台线程中执行。
 *
 */
@Service
public class MyAsyncService {

    /**
     * Spring 异步任务默认是使用全局共享的 SimpleAsyncTaskExecutor，它没有线程池，默认创建新线程。但这在高并发场景下性能较差。因此，实际中通常自定义线程池来管理异步任务。
     */
    @Async
    public void executeAsyncTask() {
        System.out.println("Task started in thread: " + Thread.currentThread().getName());
        try {
            Thread.sleep(3000);  // 模拟耗时操作
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Task completed in thread: " + Thread.currentThread().getName());
    }

    @Async("taskScheduler3")  // 使用名为 "asyncExecutor" 的线程池
    public void executeAsyncTaskWithCustomExecutor() {
        System.out.println("Task started in thread: " + Thread.currentThread().getName());
        try {
            Thread.sleep(2000);  // 模拟耗时操作
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Task completed in thread: " + Thread.currentThread().getName());
    }
}
