package com.commons.timer;

import com.commons.utils.JsonTool;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class RetryTask<T> implements TimerTask {

    /**
     * 任务名称
     */
    private final String taskName;

    /**
     * 时间次数
     */
    private int times = 1;

    /**
     * 重试次数
     */
    private final int retryTimes;

    /**
     * 重试间隔
     */
    private long retryPeriod;

    /**
     * 是否取消
     */
    private volatile boolean cancel;

    /**
     * 渠道
     */
    private Channel channel;

    /**
     * 任务
     */
    private Callable<T> task;

    public RetryTask(String taskName, Callable<T> task, Channel channel, int retryTimes, int retryPeriod) {
        this.taskName = taskName;
        this.task = task;
        this.retryTimes = retryTimes;
        this.retryPeriod = retryPeriod;
        this.channel = channel;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        if (times > retryTimes) {
            return;
        }
        T result = task.call();
        Map<String, Object> data = new HashMap<>();
        data.put("result", result);
        data.put("times", times);
        data.put("retryTimes", retryTimes);

        if (channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(JsonTool.toJSONString(data)));
            reput(timeout, retryPeriod);
        }
    }

    public void cancel() {
        cancel = true;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setRetryPeriod(int retryPeriod) {
        this.retryPeriod = retryPeriod;
    }

    protected void reput(Timeout timeout, long tick) {
        if (timeout == null) {
            throw new IllegalArgumentException();
        }

        Timer timer = timeout.timer();
        if (timeout.isCancelled() || isCancel()) {
            return;
        }

        times++;
        timer.newTimeout(timeout.task(), tick, TimeUnit.MILLISECONDS);
    }
}
