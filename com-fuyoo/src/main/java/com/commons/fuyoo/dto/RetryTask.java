package com.commons.fuyoo.dto;

import com.commons.common.utils.JsonTool;
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

    private final String taskName;

    private int times = 1;

    private final int retryTimes;

    private long retryPeriod;

    private volatile boolean cancel;

    private Channel channel;

    private Callable<T> task;

    public RetryTask(String taskName, Callable<T> task, Channel channel, int retryTimes, int retryPeriod) {
        this.taskName = taskName;
        this.task = task;
        this.retryTimes = retryTimes;
        this.retryPeriod = retryPeriod;
        this.channel = channel;

//        results = new LinkedList<>();
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

        times++; // 递增times
        // 添加定时任务
        timer.newTimeout(timeout.task(), tick, TimeUnit.MILLISECONDS);
    }
}
