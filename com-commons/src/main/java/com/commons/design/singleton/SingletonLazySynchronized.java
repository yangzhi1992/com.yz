package com.commons.design.singleton;

//懒汉式（线程安全，synchronized 锁）
/**
 * 优点
 * 线程安全，适用于多线程环境。
 * 缺点
 * 加锁会导致性能问题，尤其是高并发环境（因为每次调用 getInstance 方法时都会竞争锁）。
 */
public class SingletonLazySynchronized {
    private static SingletonLazySynchronized instance;

    private SingletonLazySynchronized() {}

    public static synchronized SingletonLazySynchronized getInstance() {
        // 确保线程安全
        if (instance == null) {
            instance = new SingletonLazySynchronized();
        }
        return instance;
    }
}
