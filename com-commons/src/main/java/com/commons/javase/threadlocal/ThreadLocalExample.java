package com.commons.javase.threadlocal;

public class ThreadLocalExample {
    // 创建 ThreadLocal 变量
    private static ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);

    public static void main(String[] args) {
        Runnable task = () -> {
            for (int i = 0; i < 5; i++) {
                int value = threadLocal.get();
                threadLocal.set(value + 1); // 修改当前线程的局部变量
                System.out.println(Thread.currentThread().getName() + ": " + threadLocal.get());
            }
        };

        // 启动多个线程
        Thread thread1 = new Thread(task, "Thread-1");
        Thread thread2 = new Thread(task, "Thread-2");

        thread1.start();
        thread2.start();

        //SimpleDateFormat线程安全见com.commons.spring.scope.ThreadSafeDateService.java
    }
}
