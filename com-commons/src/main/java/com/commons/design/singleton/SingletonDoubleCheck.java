package com.commons.design.singleton;

//双重检查锁（Double-Check Locking）
/**
 * 针对懒汉式的性能问题，采用双重检查锁机制来提高性能。
 * 优点
 * 支持懒加载，只在需要时创建实例。
 * 提高了性能，加锁只需发生一次。
 * 确保线程安全。
 * 实现比较复杂，需要理解 volatile 和双重检查锁的原理。
 */
public class SingletonDoubleCheck {
    // 使用 `volatile` 关键字，防止指令重排序，确保线程安全
    private static volatile SingletonDoubleCheck instance;

    private SingletonDoubleCheck() {}

    public static SingletonDoubleCheck getInstance() {
        if (instance == null) {
            synchronized (SingletonDoubleCheck.class) {
                if (instance == null) {
                    instance = new SingletonDoubleCheck();
                }
            }
        }
        return instance;
    }
}
