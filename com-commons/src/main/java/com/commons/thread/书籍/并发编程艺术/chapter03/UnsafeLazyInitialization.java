package com.commons.thread.书籍.并发编程艺术.chapter03;

public class UnsafeLazyInitialization {
    private static Instance instance;

    public static Instance getInstance() {
        if (instance == null) //1��A�߳�ִ��
            instance = new Instance(); //2��B�߳�ִ��
        return instance;
    }

    static class Instance {
    }
}
