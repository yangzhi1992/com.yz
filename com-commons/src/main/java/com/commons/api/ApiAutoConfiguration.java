package com.commons.api;

import com.commons.api.retrofit.RequestInterceptor;
import com.commons.api.retrofit.ResponseInterceptor;
import com.commons.api.retrofit.Retrofit;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

/**
 *
 */
@Configuration
@ConditionalOnClass({Retrofit.class, OkHttpClient.class})
@ConditionalOnProperty(prefix = "components.api", name = "enabled", matchIfMissing = false)
public class ApiAutoConfiguration {

    private final static String BASE_PACKAGES_PROPERTY_NAME = "components.api.base-packages";

    // 1. 首先配置 OkHttpClient
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(200, 5, TimeUnit.MINUTES))
                .retryOnConnectionFailure(true)
                .build();
    }

    // 2. 配置 ObjectMapper（如果尚未配置）
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        return mapper;
    }

    @Bean
    public static BeanDefinitionRegistryPostProcessor beanOverridePostProcessor() {
        return new BeanDefinitionRegistryPostProcessor() {
            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                    throws BeansException {

            }

            @Override
            public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            }
        };
    }

    @Bean
    @ConditionalOnProperty(name = BASE_PACKAGES_PROPERTY_NAME)
    @Autowired
    @DependsOn({"okHttpClient", "objectMapper"})
    public ApiConfigurer apiConfigurer(Environment environment) {
        // 使用 Binder 获取配置
        String packagesToScan = Binder.get(environment)
                                      .bind(BASE_PACKAGES_PROPERTY_NAME, String.class)
                                      .orElse("");
        return new ApiConfigurer(packagesToScan);
    }

    @ConditionalOnMissingBean(name = "defaultResponseInterceptor")
    @Bean
    public ResponseInterceptor defaultResponseInterceptor() {
        return new ResponseInterceptor.Default();
    }

    @ConditionalOnMissingBean(name = "defaultRequestInterceptor")
    @Bean
    public RequestInterceptor defaultRequestInterceptor() {
        return new RequestInterceptor.Default();
    }
}
