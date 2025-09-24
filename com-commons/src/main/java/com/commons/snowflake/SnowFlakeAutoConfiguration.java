package com.commons.snowflake;

import com.commons.common.utils.PropertiesTool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.Environment;

/**
 * 雪花算法组件
 */
@Configuration
@ConditionalOnProperty(prefix = "components.snowflake", name = "enabled", matchIfMissing = false)
public class SnowFlakeAutoConfiguration implements ApplicationListener<ContextClosedEvent>, EnvironmentAware,
        ApplicationContextAware {
    private ApplicationContext applicationContext;
    private Environment environment;
    private SnowflakeIdWorker worker;
    @Value("${app.id:}")
    private String appId;

    @Bean
    @ConditionalOnMissingBean
    public SnowflakeProperties snowflakeProperties() {
        SnowflakeProperties properties = new SnowflakeProperties();
        PropertiesTool.bind(properties, environment);
        return properties;
    }

    @Bean
    public SnowflakeIdWorker snowflakeConfig() {
        SnowflakeProperties prop = snowflakeProperties();
        if (StringUtils.isBlank(appId)) {
            throw new RuntimeException("app.id is not exist");
        }
        if (StringUtils.isBlank(prop.getRedisTemplate())) {
            throw new RuntimeException(prop.getRedisTemplate() + " is not exist");
        }
        this.worker = new SnowflakeIdWorker(appId, prop, applicationContext);
        return this.worker;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (this.worker != null) {
            this.worker.destroy();
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

