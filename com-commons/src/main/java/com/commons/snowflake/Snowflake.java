package com.commons.snowflake;

import lombok.Getter;

public class Snowflake {
    /**
     * 开始时间粒
     */
    private final long START_SECOND;

    /**
     * 机器ID向左移位数
     */
    private final long workerIdShift;
    /**
     * 时间粒向左移位数
     */
    private final long timestampLeftShift;
    /**
     * 生成序列的掩码
     */
    private final long sequenceMask;
    /**
     * 工作机器ID
     */
    @Getter
    private final long workerId;
    /**
     * 时间类型
     */
    private final boolean millisecond;
    /**
     * 时间粒内序列
     */
    private long sequence = 0L;
    /**
     * 上次生成ID的时间粒
     */
    private long lastTime = -1L;

    /**
     * 构造函数
     */
    public Snowflake(long workerId, boolean millisecond, long startTimestamp, long workerIdBits, long sequenceBits) {
        this.workerId = workerId;
        // 支持的机器id最大值
        long maxWorkerId = ~(-1L << workerIdBits);
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        // 支持的序列最大值
        this.sequenceMask = ~(-1L << sequenceBits);
        this.workerIdShift = sequenceBits;
        this.millisecond = millisecond;
        this.START_SECOND = millisecond ? startTimestamp : (startTimestamp / 1000);
        this.timestampLeftShift = workerIdShift + workerIdBits;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     */
    public synchronized long nextId() {
        long time = timeGen();

        // 处理时钟回拨
        if (time < lastTime) {
            long offset = lastTime - time;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    time = timeGen();
                    if (time < lastTime) {
                        throw new RuntimeException("Clock moved backwards after waiting");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException("Clock moved backwards. Refusing to generate id");
            }
        }

        // 如果是同一时间生成的，则进行时间粒内自增
        if (lastTime == time) {
            sequence = (sequence + 1) & sequenceMask;
            // 时间粒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个时间粒,获得新的时间戳
                time = tilNext(lastTime);
            }
        }
        // 时间戳改变，序列重置
        else {
            sequence = 0L;
        }

        // 上次生成ID的时间粒
        lastTime = time;

        // 移位并通过或运算拼到一起组成二进制ID
        return ((time - START_SECOND) << timestampLeftShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    /**
     * 阻塞到下一个时间粒，直到获得新的时间粒
     *
     * @param lastTime 上次生成ID的时间粒
     */
    protected long tilNext(long lastTime) {
        long time = timeGen();
        while (time <= lastTime) {
            time = timeGen();
        }
        return time;
    }

    /**
     * 返回以时间粒为单位的当前时间
     */
    protected long timeGen() {
        return millisecond ? System.currentTimeMillis() : System.currentTimeMillis() / 1000L;
    }

}
