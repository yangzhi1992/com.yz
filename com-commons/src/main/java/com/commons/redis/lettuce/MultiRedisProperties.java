package com.commons.redis.lettuce;

import io.lettuce.core.ReadFrom;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis.multi")
public class MultiRedisProperties {
    
    private boolean enabled = false;
    
    private Map<String, RedisConfig> datasources = new HashMap<>();
    
    @Data
    public static class RedisConfig {
        private RedisMode mode = RedisMode.SINGLE;
        private String host;
        private int port = 6379;
        private int database = 0;
        private String password;
        private Duration timeout;
        private LettuceConfig lettuce;
        private ClusterConfig cluster;
        private MasterConfig master;
        private List<SlaveConfig> slaves;
        private ReadFrom readFrom = ReadFrom.REPLICA_PREFERRED; // 读策略
    }
    
    @Data
    public static class LettuceConfig {
        private PoolConfig pool;
        private Duration shutdownTimeout;
        private ReadFrom readFrom = ReadFrom.REPLICA_PREFERRED; // 读策略
    }
    
    @Data
    public static class PoolConfig {
        private int maxActive = 8;
        private int maxIdle = 8;
        private int minIdle = 0;
        private Duration maxWait = Duration.ofMillis(-1);
    }
    
    @Data
    public static class ClusterConfig {
        private List<String> nodes;
        private Integer maxRedirects;
    }
    
    @Data
    public static class MasterConfig {
        private String host;
        private int port = 6379;
    }
    
    @Data
    public static class SlaveConfig {
        private String host;
        private int port = 6379;
    }
    
    public enum RedisMode {
        SINGLE, MASTER_SLAVE, CLUSTER
    }

    // ReadFrom 策略枚举
    public enum ReadFrom {
        MASTER, MASTER_PREFERRED, REPLICA, REPLICA_PREFERRED, ANY, ANY_REPLICA
    }
}