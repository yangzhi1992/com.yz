package com.commons.design.singleton;

//恶汉式单例-该实现方式会在类加载时就立即创建唯一的实例。
/**
 * 优点
 * 简单易懂，线程安全。
 * 类加载时就实例化，避免了多线程问题。
 * 缺点
 * 即使没有使用此类，实例也会被提前创建，占用资源（浪费资源）。
 * 不支持延迟加载功能。
 */
public class SingletonHungry {
    // 提前创建单例对象
    private static final SingletonHungry INSTANCE = new SingletonHungry();

    // 私有化构造方法，防止外界直接实例化
    private SingletonHungry() {
    }

    // 全局访问点，提供单例实例
    public static SingletonHungry getInstance() {
        return INSTANCE;
    }
}