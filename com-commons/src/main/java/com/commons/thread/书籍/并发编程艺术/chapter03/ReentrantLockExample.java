package com.commons.thread.书籍.并发编程艺术.chapter03;

import java.util.concurrent.locks.ReentrantLock;

class ReentrantLockExample {
    int           a    = 0;
    ReentrantLock lock = new ReentrantLock();

    public void writer() {
        lock.lock(); //��ȡ��
        try {
            a++;
        } finally {
            lock.unlock(); //�ͷ���
        }
    }

    public void reader() {
        lock.lock(); //��ȡ��
        try {
            int i = a;
            //����
        } finally {
            lock.unlock(); //�ͷ���
        }
    }
}
