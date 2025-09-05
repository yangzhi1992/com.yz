package com.commons.thread.书籍.并发编程艺术.chapter03;

class VolatileFeaturesExample {
    volatile long vl = 0L; //ʹ��volatile����64λ��long�ͱ���

    public void set(long l) {
        vl = l; //����volatile������д
    }

    public void getAndIncrement() {
        vl++; //���ϣ������volatile�����Ķ�/д
    }

    public long get() {
        return vl; //����volatile�����Ķ�
    }
}
