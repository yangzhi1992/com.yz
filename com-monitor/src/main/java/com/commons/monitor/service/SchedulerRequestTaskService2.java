package com.commons.monitor.service;

import io.netty.channel.Channel;
import java.util.concurrent.Callable;

public interface SchedulerRequestTaskService2<T> {

    void registerTask(Callable<T> runnable, Channel channel, String taskName, int delay);

    void unregisterTask(String taskName);
}
