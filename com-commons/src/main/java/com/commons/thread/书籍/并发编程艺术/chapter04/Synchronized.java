package com.commons.thread.书籍.并发编程艺术.chapter04;

/**
 * 6-10
 */
public class Synchronized {
    public static void main(String[] args) {
        // ��Synchronized Class������м���
        synchronized (Synchronized.class) {

        }
        // ��̬ͬ����������Synchronized Class������м���
        m();
    }

    public static synchronized void m() {
    }
}
