package com.commons.api;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;
import static org.springframework.util.ClassUtils.resolveClassName;

import com.commons.api.annotation.ApiClass;
import com.commons.common.utils.CollectionTool;
import com.commons.common.utils.StringTool;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

/**
 *
 */
public class ApiConfigurer implements BeanDefinitionRegistryPostProcessor, EnvironmentAware,
        BeanClassLoaderAware,
        ResourceLoaderAware {

    private Environment environment;

    private ClassLoader classLoader;

    private ResourceLoader resourceLoader;

    private String packagesToScan;

    public ApiConfigurer(String packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        ApiClassPathBeanDefinitionScanner scanner = new ApiClassPathBeanDefinitionScanner(
                registry, false, environment, resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ApiClass.class));

        for (String packageToScan : resolvePackagesToScan(
                CollectionTool.asSet(StringTool.split(packagesToScan, ',')))) {
            Set<BeanDefinitionHolder> holders = scanner.doScan(packageToScan);
            holders.stream().filter(holder -> holder
                    .getBeanDefinition() instanceof ScannedGenericBeanDefinition)
                    .forEach(holder -> {
                        ScannedGenericBeanDefinition definition = (ScannedGenericBeanDefinition) holder
                                .getBeanDefinition();
                        Class<?> beanClass = resolveClass(definition);
                        AnnotationAttributes attributes = AnnotationAttributes
                                .fromMap(definition.getMetadata()
                                        .getAnnotationAttributes(ApiClass.class.getName(), true));
                        String key = attributes.getString("value");
                        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(
                                ApiFactoryBean.class)
                                .addConstructorArgValue(beanClass)
                                .addConstructorArgValue(attributes.getString("baseUrl"))
                                .addPropertyReference("objectMapper", "objectMapper")
                                .addPropertyReference("originalClient", "okHttpClient")
                                .addPropertyReference("requestInterceptor",
                                        attributes.getString("requestInterceptor"))
                                .addPropertyReference("responseInterceptor",
                                        attributes.getString("responseInterceptor"));
                        registry.registerBeanDefinition(
                                key,
                                builder.getBeanDefinition());
                    });
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {

    }

    private Class<?> resolveClass(BeanDefinition beanDefinition) {
        String beanClassName = beanDefinition.getBeanClassName();
        return resolveClassName(beanClassName, classLoader);

    }

    private Set<String> resolvePackagesToScan(Set<String> packagesToScan) {
        Set<String> resolvedPackagesToScan = new LinkedHashSet<>(packagesToScan.size());
        for (String packageToScan : packagesToScan) {
            if (StringUtils.hasText(packageToScan)) {
                String resolvedPackageToScan = environment
                        .resolvePlaceholders(packageToScan.trim());
                resolvedPackagesToScan.add(resolvedPackageToScan);
            }
        }
        return resolvedPackagesToScan;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
