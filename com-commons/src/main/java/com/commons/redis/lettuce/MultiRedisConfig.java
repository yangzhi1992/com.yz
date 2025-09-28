package com.commons.redis.lettuce;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import io.lettuce.core.ReadFrom;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableConfigurationProperties(MultiRedisProperties.class)
public class MultiRedisConfig {
    
    private final MultiRedisProperties multiRedisProperties;
    
    public MultiRedisConfig(MultiRedisProperties multiRedisProperties) {
        this.multiRedisProperties = multiRedisProperties;
    }
    
    @Bean
    @Primary
    public RedisDataSourceManager redisDataSourceManager() {
        RedisDataSourceManager manager = new RedisDataSourceManager();
        
        if (!multiRedisProperties.isEnabled()) {
            return manager;
        }
        
        multiRedisProperties.getDatasources().forEach((name, config) -> {
            RedisConnectionFactory factory = createConnectionFactory(config, name);
            RedisTemplate<String, Object> template = createRedisTemplate(factory);
            StringRedisTemplate stringTemplate = createStringRedisTemplate(factory);
            
            manager.addDataSource(name, factory, template, stringTemplate);
        });
        
        return manager;
    }
    
    private RedisConnectionFactory createConnectionFactory(MultiRedisProperties.RedisConfig config, String dataSourceName) {
        LettuceClientConfiguration clientConfig = createLettuceClientConfiguration(config);
        
        switch (config.getMode()) {
            case SINGLE:
                return createSingleConnectionFactory(config, clientConfig);
            case MASTER_SLAVE:
                return createMasterSlaveConnectionFactory(config, clientConfig);
            case CLUSTER:
                return createClusterConnectionFactory(config, clientConfig);
            default:
                throw new IllegalArgumentException("不支持的Redis模式: " + config.getMode());
        }
    }
    
    private RedisConnectionFactory createSingleConnectionFactory(
            MultiRedisProperties.RedisConfig config, 
            LettuceClientConfiguration clientConfig) {
        
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
        standaloneConfig.setHostName(config.getHost());
        standaloneConfig.setPort(config.getPort());
        standaloneConfig.setDatabase(config.getDatabase());
        if (config.getPassword() != null) {
            standaloneConfig.setPassword(RedisPassword.of(config.getPassword()));
        }
        
        return new LettuceConnectionFactory(standaloneConfig, clientConfig);
    }
    
    private RedisConnectionFactory createMasterSlaveConnectionFactory(
            MultiRedisProperties.RedisConfig config, 
            LettuceClientConfiguration clientConfig) {
        
        RedisStaticMasterReplicaConfiguration masterSlaveConfig =
            new RedisStaticMasterReplicaConfiguration(config.getMaster().getHost(), config.getMaster().getPort());
        
        // 添加从节点
        if (config.getSlaves() != null) {
            config.getSlaves().forEach(slave -> {
                masterSlaveConfig.addNode(slave.getHost(), slave.getPort());
            });
        }
        
        masterSlaveConfig.setDatabase(config.getDatabase());
        if (config.getPassword() != null) {
            masterSlaveConfig.setPassword(RedisPassword.of(config.getPassword()));
        }
        
        LettuceConnectionFactory factory = new LettuceConnectionFactory(masterSlaveConfig, clientConfig);
        factory.afterPropertiesSet();
        return factory;
    }

    private ReadFrom resolveReadFrom(MultiRedisProperties.RedisConfig config) {
        // 优先使用master-slave配置中的读策略
        if (config != null && config.getReadFrom() != null) {
            return convertReadFrom(config.getReadFrom());
        }
        // 其次使用lettuce配置中的读策略
        if (config.getLettuce().getReadFrom() != null) {
            return convertReadFrom(config.getLettuce().getReadFrom());
        }
        // 默认策略
        return ReadFrom.MASTER_PREFERRED;
    }

    private ReadFrom convertReadFrom(MultiRedisProperties.ReadFrom readFrom) {
        switch (readFrom) {
            case MASTER: return ReadFrom.MASTER;
            case MASTER_PREFERRED: return ReadFrom.MASTER_PREFERRED;
            case REPLICA: return ReadFrom.REPLICA;
            case REPLICA_PREFERRED: return ReadFrom.REPLICA_PREFERRED;
            case ANY: return ReadFrom.ANY;
            case ANY_REPLICA: return ReadFrom.ANY_REPLICA;
            default: return ReadFrom.MASTER_PREFERRED;
        }
    }
    
    private RedisConnectionFactory createClusterConnectionFactory(
            MultiRedisProperties.RedisConfig config, 
            LettuceClientConfiguration clientConfig) {
        
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration();
        
        // 设置集群节点
        if (config.getCluster() != null && config.getCluster().getNodes() != null) {
            config.getCluster().getNodes().forEach(node -> {
                String[] parts = node.split(":");
                clusterConfig.addClusterNode(new RedisNode(parts[0], Integer.parseInt(parts[1])));
            });
        }
        
        if (config.getCluster() != null && config.getCluster().getMaxRedirects() != null) {
            clusterConfig.setMaxRedirects(config.getCluster().getMaxRedirects());
        }
        
        if (config.getPassword() != null) {
            clusterConfig.setPassword(RedisPassword.of(config.getPassword()));
        }
        
        return new LettuceConnectionFactory(clusterConfig, clientConfig);
    }
    
    private LettucePoolingClientConfiguration createLettuceClientConfiguration(MultiRedisProperties.RedisConfig config) {
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder =
                LettucePoolingClientConfiguration.builder();

        // 超时配置
        if (config.getTimeout() != null) {
            builder.commandTimeout(config.getTimeout());
        }

        // 连接池配置
        if (config.getLettuce() != null && config.getLettuce().getPool() != null) {
            GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
            MultiRedisProperties.PoolConfig poolProps = config.getLettuce().getPool();

            poolConfig.setMaxTotal(poolProps.getMaxActive());
            poolConfig.setMaxIdle(poolProps.getMaxIdle());
            poolConfig.setMinIdle(poolProps.getMinIdle());
            poolConfig.setMaxWaitMillis(poolProps.getMaxWait().toMillis());

            // 正确的配置方式：使用 usePool() 并传入 PoolConfig
            builder.poolConfig(poolConfig);
        }

        // 读策略配置（重要：用于主从识别和路由）
        if (config.getMode() == MultiRedisProperties.RedisMode.MASTER_SLAVE) {
            // 主从模式设置读策略
            ReadFrom readFrom = resolveReadFrom(config);
            builder.readFrom(readFrom);
        }

        // 关闭超时
        if (config.getLettuce() != null && config.getLettuce().getShutdownTimeout() != null) {
            builder.shutdownTimeout(config.getLettuce().getShutdownTimeout());
        }

        // SSL配置（可选）
        builder.useSsl().disablePeerVerification();

        return builder.build();
    }
    
    private RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 使用Jackson序列化
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 正确的多态类型验证配置
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                                                                    .allowIfSubType(Object.class)
                                                                    .build();

        mapper.activateDefaultTyping(
                ptv,
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        serializer.setObjectMapper(mapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }
    
    private StringRedisTemplate createStringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        return template;
    }
}