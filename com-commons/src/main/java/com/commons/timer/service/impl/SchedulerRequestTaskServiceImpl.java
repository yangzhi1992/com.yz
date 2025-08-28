package com.commons.timer.service.impl;

import com.commons.timer.RetryTask;
import com.commons.timer.service.SchedulerRequestTaskService;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import io.netty.channel.Channel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class SchedulerRequestTaskServiceImpl<T> implements SchedulerRequestTaskService<T> {

    private Timer timer = null;

    private Map<String, RetryTask<T>> timerTaskMap = new HashMap<>();

    private final int defaultRetryTimes = 100;

    @PostConstruct
    public void init() {
        this.timer = new HashedWheelTimer(new NamedThreadFactory("SchedulerRequestTaskServiceTimer"), 100,
                TimeUnit.MILLISECONDS, 128);
    }

    @Override
    public void registerTask(Callable<T> runnable, Channel channel, String taskName, int delay) {
        RetryTask<T> task = new RetryTask<T>(taskName, runnable, channel, defaultRetryTimes, delay);

        RetryTask<T> oldOne = timerTaskMap.putIfAbsent(taskName, task);
        if (oldOne == null) {
            timer.newTimeout(task, delay, TimeUnit.MILLISECONDS);
        } else {
            oldOne.setRetryPeriod(delay);
        }
    }

    @Override
    public void unregisterTask(String taskName) {
        final RetryTask<T> task = timerTaskMap.remove(taskName);
        if (task != null) {
            task.cancel();
        }
    }

}
