package com.commons.snowflake;

import com.commons.common.utils.NetTool;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;

public class SnowflakeIdWorker {
    private final ApplicationContext applicationContext;
    private final SnowflakeProperties conf;
    private final String appId;
    private final String lockKey;
    private final String workerKey;
    private final String self;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private Snowflake snowFlake;
    private boolean randomInit = false;

    public SnowflakeIdWorker(String appId, SnowflakeProperties conf, ApplicationContext applicationContext) {
        this.appId = appId;
        this.applicationContext = applicationContext;
        this.conf = conf;
        String REDIS_KEY_PREFIX = "snowflake:";
        this.lockKey = REDIS_KEY_PREFIX + appId + ":lock";
        this.workerKey = REDIS_KEY_PREFIX + appId;
        this.self = NetTool.getSnowflakeWorker();
        if (this.self == null) {
            throw new RuntimeException("获取机器码失败");
        }
    }

    @PostConstruct
    private void init() {
        snowFlake = new Snowflake(Long.parseLong("1"), conf.isMillisecond(),
                conf.getStartTimestamp(), conf.getWorkerBits(), conf.getSequenceBits());
        this.heartbeatStart();
    }

    /**
     * 发送心跳
     */
    private void heartbeatStart() {
        executor.scheduleAtFixedRate(() -> {
        }, conf.getHeartbeatSecond(), conf.getHeartbeatSecond(), TimeUnit.SECONDS);
    }

    /**
     * 容器销毁
     */
    public void destroy() {
    }

    /**
     * 获取ID
     */
    public long nextId() {
        return snowFlake.nextId();
    }

}
