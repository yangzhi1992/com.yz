package com.commons.snowflake;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "components.snowflake")
public class SnowflakeProperties {
    /**
     * 机器位，默认10
     */
    private int workerBits = 10;
    /**
     * 每个时间粒度内自增位数量，默认12
     */
    public long sequenceBits = 12L;

    /**
     * 心跳时间，默认30s
     */
    private int heartbeatSecond = 30;

    /**
     * 心跳过期宽限时间，默认1s
     */
    private int heartbeatGraceSecond = 1;

    /**
     * 时间类型，默认毫秒，否则是秒
     */
    private boolean millisecond = true;

    /**
     * 开始时间戳，默认2019-07-26
     */
    private long startTimestamp = 1564128000000L;

    /**
     * redis 异常时使用随机数初始化
     */
    private boolean redisErrorRandomInit = false;

    /**
     * 获取锁休息时间
     */
    private int tryLockSleepMs = 100;

    /**
     * 锁过期时间
     */
    private long lockExpireSecond = 5L;

    /**
     * 获取锁最大尝试次数
     */
    private int tryLockMaxTimes = 100;

    /**
     * redis 实例Bean
     */
    private String redisTemplate;
}
