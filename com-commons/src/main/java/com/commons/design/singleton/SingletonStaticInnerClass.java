package com.commons.design.singleton;
//静态内部类单例（推荐）
/**
 *  Java 的类加载机制能够确保，类的静态内部类只有在被首次加载时才会加载，并且加载时线程安全。
 *  静态内部类方式是实现单例模式的一种推荐方法，既支持延迟加载，又避免了线程安全问题。
 *  优点
 * 线程安全：借助 Java 本身类加载机制，静态内部类只会被加载一次。
 * 支持延迟加载：Singleton 的实例会在 getInstance() 方法调用时初始化。
 * 性能高：避免了 synchronized 导致的性能开销。
 */
public class SingletonStaticInnerClass {
    private SingletonStaticInnerClass() {}

    // 静态内部类
    private static class SingletonHolder {
        private static final SingletonStaticInnerClass INSTANCE = new SingletonStaticInnerClass();
    }

    // 提供全局访问点
    public static SingletonStaticInnerClass getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
