package com.commons.timer.service;

import io.netty.channel.Channel;
import java.util.concurrent.Callable;

public interface SchedulerRequestTaskService<T> {

    void registerTask(Callable<T> runnable, Channel channel, String taskName, int delay);

    void unregisterTask(String taskName);
}
