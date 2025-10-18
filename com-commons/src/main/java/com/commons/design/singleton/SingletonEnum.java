package com.commons.design.singleton;

//枚举单例-使用枚举可以完全避免线程安全问题和反序列化问题，因为 Java 中 enum 本身保证了单例性。
/**
 * 简洁、无懈可击。
 * Java 枚举的机制可以完全避免反射攻击和序列化导致的问题。
 */
public enum SingletonEnum {
    INSTANCE; // 唯一实例

    public void someMethod() {
        System.out.println("Hello from Singleton!");
    }

    public static class Main {
        public static void main(String[] args) {
            SingletonEnum singleton = SingletonEnum.INSTANCE;
            singleton.someMethod();
        }
    }
}
