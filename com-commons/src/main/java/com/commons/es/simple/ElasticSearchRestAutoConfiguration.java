package com.commons.es.simple;

import com.commons.common.exception.BusinessException;
import com.commons.common.utils.CollectionTool;
import com.commons.common.utils.PropertiesTool;
import com.commons.es.simple.props.ElasticSearchProperties;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * ES 配置类
 *
 * @author xuxiang@qiyi.com
 * @since 2019/8/6 14:19
 */
@Configuration
@ConditionalOnClass(RestHighLevelClient.class)
@ConditionalOnProperty(prefix = "components.elasticsearch", name = "enabled", matchIfMissing = false)
public class ElasticSearchRestAutoConfiguration implements EnvironmentAware {

    private Environment environment;

    @Bean
    @ConditionalOnMissingBean
    public ElasticSearchProperties elasticSearchProperties() {
        ElasticSearchProperties elasticSearchProperties = new ElasticSearchProperties();
        PropertiesTool.bind(elasticSearchProperties, environment);
        if (CollectionTool.isBlank(elasticSearchProperties.getElastic())) {
            throw new BusinessException("elastic search初始化失败,配置参数为空!");
        }
        return elasticSearchProperties;
    }

    @Bean
    @Autowired
    @ConditionalOnMissingBean
    public ElasticSearchRestConfigure elasticSearchConfigure(ElasticSearchProperties elasticSearchProperties) {
        return new ElasticSearchRestConfigure(elasticSearchProperties);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
