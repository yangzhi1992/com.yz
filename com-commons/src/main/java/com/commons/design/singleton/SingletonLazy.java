package com.commons.design.singleton;

//懒汉式单例 - 这种方式可以实现延迟加载（Lazy Initialization），即在需要时才创建对象。
/**
 * 优点
 * 支持延迟加载，在调用 getInstance() 方法时才会创建实例。
 * 在单线程环境下，这种方式线程安全，开销低。
 * 缺点
 * 线程不安全：如果在多线程环境下，多个线程可能同时调用 getInstance()，导致创建多个实例。
 * 不能用于多线程场景，需要额外处理同步问题。
 */
public class SingletonLazy {
    private static SingletonLazy instance; // 不急于创建对象

    // 私有化构造方法，防止外部实例化
    private SingletonLazy() {}

    // 提供一个全局访问点
    public static SingletonLazy getInstance() {
        // 第一次使用时才实例化
        if (instance == null) {
            instance = new SingletonLazy();
        }
        return instance;
    }
}
