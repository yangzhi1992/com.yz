package com.commons.thread.yield;

public class YieldTest {
    public static void main(String[] args) {
        YieldTest threadMethod = new YieldTest();
        threadMethod.yidld();
    }

    private void yidld() {
        YieldThread thread = new YieldThread();
        thread.start();
    }

    /**
     * yield()方法的作用是放弃当前cpu资源，将他让给其他任务去占用cpu执行的时间，
     * 放弃的时间不确定有可能马上放弃，马上又获得cpu时间片
     * 将Thread.yield();注释掉和不注释掉看程序执行时间
     */
    private class YieldThread extends Thread {
        @Override
        public void run() {
            long beginTime = System.currentTimeMillis();
            int count = 0;
            for (int i = 0; i < 100000; i++) {
                Thread.yield();
                count = count + 1;
            }
            long endTime = System.currentTimeMillis();
            System.out.println("usetime:" + (endTime - beginTime));
        }
    }
}
