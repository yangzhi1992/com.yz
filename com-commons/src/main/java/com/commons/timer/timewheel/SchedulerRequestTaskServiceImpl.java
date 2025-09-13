package com.commons.timer.timewheel;

import io.micrometer.core.instrument.util.NamedThreadFactory;
import io.netty.channel.Channel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Component
public class SchedulerRequestTaskServiceImpl {

    private Timer timer = null;

    private Map<String, RetryTask> timerTaskMap = new HashMap<>();

    private final int defaultRetryTimes = 100;

    @PostConstruct
    public void init() {
        this.timer = new HashedWheelTimer(new NamedThreadFactory(
                "SchedulerRequestTaskServiceTimer"),
                100, TimeUnit.MILLISECONDS, 128);
    }


    public void registerTask(Callable runnable, Channel channel, String taskName, int delay) {
        RetryTask task = new RetryTask(taskName, runnable, channel, defaultRetryTimes, delay);

        RetryTask oldOne = timerTaskMap.putIfAbsent(taskName, task);
        if (oldOne == null) {
            timer.newTimeout(task, delay, TimeUnit.MILLISECONDS);
        } else {
            oldOne.setRetryPeriod(delay);
        }
    }

    public void unregisterTask(String taskName) {
        final RetryTask task = timerTaskMap.remove(taskName);
        if (task != null) {
            task.cancel();
        }
    }

}
