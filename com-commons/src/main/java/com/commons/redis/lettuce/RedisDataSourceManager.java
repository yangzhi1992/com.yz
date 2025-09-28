package com.commons.redis.lettuce;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisDataSourceManager {
    
    private final Map<String, RedisConnectionFactory> connectionFactories = new ConcurrentHashMap<>();
    private final Map<String, RedisTemplate<String, Object>> redisTemplates = new ConcurrentHashMap<>();
    private final Map<String, StringRedisTemplate> stringRedisTemplates = new ConcurrentHashMap<>();
    
    public void addDataSource(String name, 
                            RedisConnectionFactory connectionFactory,
                            RedisTemplate<String, Object> redisTemplate,
                            StringRedisTemplate stringRedisTemplate) {
        connectionFactories.put(name, connectionFactory);
        redisTemplates.put(name, redisTemplate);
        stringRedisTemplates.put(name, stringRedisTemplate);
    }
    
    public RedisConnectionFactory getConnectionFactory(String dataSourceName) {
        RedisConnectionFactory factory = connectionFactories.get(dataSourceName);
        if (factory == null) {
            throw new IllegalArgumentException("未知的数据源: " + dataSourceName);
        }
        return factory;
    }
    
    public RedisTemplate<String, Object> getRedisTemplate(String dataSourceName) {
        RedisTemplate<String, Object> template = redisTemplates.get(dataSourceName);
        if (template == null) {
            throw new IllegalArgumentException("未知的数据源: " + dataSourceName);
        }
        return template;
    }
    
    public StringRedisTemplate getStringRedisTemplate(String dataSourceName) {
        StringRedisTemplate template = stringRedisTemplates.get(dataSourceName);
        if (template == null) {
            throw new IllegalArgumentException("未知的数据源: " + dataSourceName);
        }
        return template;
    }
    
    public Set<String> getDataSourceNames() {
        return connectionFactories.keySet();
    }
    
    public boolean containsDataSource(String dataSourceName) {
        return connectionFactories.containsKey(dataSourceName);
    }
}