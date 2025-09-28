package com.commons.redis.lettuce;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MultiRedisService {
    
    private final RedisDataSourceManager dataSourceManager;
    
    public MultiRedisService(RedisDataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }
    
    /**
     * 设置值
     */
    public <T> void set(String dataSourceName, String key, T value) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        template.opsForValue().set(key, value);
    }
    
    /**
     * 设置值并指定过期时间
     */
    public <T> void set(String dataSourceName, String key, T value, long timeout, TimeUnit unit) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        template.opsForValue().set(key, value, timeout, unit);
    }
    
    /**
     * 获取值
     */
    public <T> T get(String dataSourceName, String key, Class<T> clazz) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        Object value = template.opsForValue().get(key);
        return clazz.cast(value);
    }
    
    /**
     * 删除键
     */
    public Boolean delete(String dataSourceName, String key) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        return template.delete(key);
    }
    
    /**
     * 设置过期时间
     */
    public Boolean expire(String dataSourceName, String key, long timeout, TimeUnit unit) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        return template.expire(key, timeout, unit);
    }
    
    /**
     * Hash操作 - 设置字段值
     */
    public void hSet(String dataSourceName, String key, String field, Object value) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        template.opsForHash().put(key, field, value);
    }
    
    /**
     * Hash操作 - 获取字段值
     */
    public Object hGet(String dataSourceName, String key, String field) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        return template.opsForHash().get(key, field);
    }
    
    /**
     * 获取数据源信息
     */
    public RedisInfo getRedisInfo(String dataSourceName) {
        RedisConnectionFactory factory = dataSourceManager.getConnectionFactory(dataSourceName);
        RedisConnection connection = factory.getConnection();
        
        try {
            Properties info = connection.info();
            RedisInfo redisInfo = new RedisInfo();
            redisInfo.setDataSourceName(dataSourceName);
            redisInfo.setVersion(info.getProperty("redis_version"));
            redisInfo.setMode(info.getProperty("redis_mode"));
            redisInfo.setConnectedClients(info.getProperty("connected_clients"));
            redisInfo.setUsedMemory(info.getProperty("used_memory_human"));
            
            return redisInfo;
        } finally {
            connection.close();
        }
    }
    
    /**
     * 执行流水线操作
     */
    public List<Object> executePipeline(String dataSourceName, RedisCallback<?> action) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        return template.executePipelined(action);
    }
    
    @Data
    public static class RedisInfo {
        private String dataSourceName;
        private String version;
        private String mode;
        private String connectedClients;
        private String usedMemory;
    }
}