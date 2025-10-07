package com.commons.elasticsearch.simple;

import com.commons.common.exception.BusinessException;
import com.commons.common.utils.CollectionTool;
import com.commons.common.utils.PropertiesTool;
import com.commons.common.utils.StringTool;
import com.commons.elasticsearch.simple.dto.ElasticConfigItem;
import com.commons.elasticsearch.simple.props.ElasticNode;
import com.commons.elasticsearch.simple.props.ElasticSearchProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/**
 * 自动注册 ElasticSearchFactoryBean
 */
public class ElasticSearchRestConfigure implements BeanDefinitionRegistryPostProcessor,
        EnvironmentAware, ApplicationListener<ContextClosedEvent>, ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(ElasticSearchRestAutoConfiguration.class);

    private Environment environment;

    private ConfigurableListableBeanFactory context;

    private List<ElasticSearchRestFactoryBean> factoryList = new ArrayList<>();

    private ElasticSearchProperties elasticSearchProperties;

    private Object mutex = new Object();

    private volatile boolean stopped = false;

    public ElasticSearchRestConfigure(ElasticSearchProperties elasticSearchProperties) {
        this.elasticSearchProperties = elasticSearchProperties;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Map<String, ElasticConfigItem> configItemMap = elasticSearchProperties.getElastic();
        if (CollectionTool.isBlank(configItemMap)) {
            throw new BusinessException("elastic search初始化失败,配置参数为空!");
        }
        for (Map.Entry<String, ElasticConfigItem> entry : configItemMap.entrySet()) {
            String key = entry.getKey();
            ElasticConfigItem elasticConfigItem = entry.getValue();

            List<ElasticNode> nodes = elasticConfigItem.getNodes();
            List<HttpHost> nodeList = new ArrayList<>();
            for (ElasticNode node : nodes) {
                Assert.hasText(node.getHost(), "[Assertion failed] missing host name in 'nodes'");
                nodeList.add(new HttpHost(node.getHost(), node.getPort()));
            }
            ElasticSearchRestFactoryBean elasticFactoryBean = new ElasticSearchRestFactoryBean(nodeList
                    .toArray(new HttpHost[0]), elasticConfigItem.getUsername(), elasticConfigItem.getPassword(),
                    elasticConfigItem.getProperties());
            factoryList.add(elasticFactoryBean);
            boolean lazyInit = PropertiesTool.isLazy(environment, key, "elastic");
            if (!lazyInit) {//如果非延迟加载,调用init方法
                elasticFactoryBean.init();
            }
            context.registerSingleton(StringTool.underscoreToCamelCase(key) + "RestClient",
                    elasticFactoryBean);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.warn("Invoke close method on ElasticSearchRestFactoryBean");
        synchronized (mutex) {
            if (stopped) {
                return;
            }
            this.stopped = true;
            factoryList.forEach(ElasticSearchRestFactoryBean::close);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            this.context = ((ConfigurableApplicationContext)applicationContext).getBeanFactory();
        }
    }
}
