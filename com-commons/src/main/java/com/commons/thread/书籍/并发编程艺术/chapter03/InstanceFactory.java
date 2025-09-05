package com.commons.thread.书籍.并发编程艺术.chapter03;

public class InstanceFactory {
    private static class InstanceHolder {
        public static Instance instance = new Instance();
    }

    public static Instance getInstance() {
        return InstanceHolder.instance; //���ｫ����InstanceHolder�౻��ʼ��
    }

    static class Instance {
    }
}
