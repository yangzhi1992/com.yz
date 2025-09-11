package com.commons.common.utils;

import com.commons.common.support.FrameworkConstants;
import java.util.Properties;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 *
 */
public final class PropertiesTool {

    /**
     * 判断是否延迟加载
     */
    public static boolean isLazy(Environment environment, String key, String resourceType) {
        String key1 = FrameworkConstants.LAZY_PREFIX + "." + key + "." + resourceType + ".enabled";
        String key2 =
                FrameworkConstants.LAZY_PREFIX + "." + StringTool.underscoreToCamelCase(key) + "."
                        + resourceType
                        + ".enabled";
        if (environment.containsProperty(key1)) {
            return environment.getProperty(key1, Boolean.class);
        } else if (environment
                .containsProperty(key2)) {
            return environment.getProperty(key2, Boolean.class);
        } else {
            return true;
        }
    }

    public static void bind(Object target, Environment environment) {
        // 1. 获取属性源（兼容所有环境类型）
        Iterable<ConfigurationPropertySource> propertySources = ConfigurationPropertySources.get(environment);

        // 2. 创建 Binder 实例
        Binder binder = new Binder(propertySources);

        // 3. 处理 @ConfigurationProperties 注解
        ConfigurationProperties annotation = AnnotationUtils.findAnnotation(
                target.getClass(), ConfigurationProperties.class
        );

        // 4. 构建绑定处理器
        BindHandler bindHandler = createBindHandler(annotation);

        try {
            // 5. 执行绑定
            if (annotation != null && StringUtils.hasText(annotation.prefix())) {
                // 带前缀的绑定
                binder.bind(annotation.prefix(), Bindable.ofInstance(target).withAnnotations(annotation), bindHandler);
            } else {
                // 无前缀的绑定
                binder.bind("", Bindable.ofInstance(target), bindHandler);
            }
        } catch (BindException ex) {
            // 6. 异常处理（保持与原实现相同）
            String targetClass = ClassUtils.getShortName(target.getClass());
            throw new BeanCreationException(
                    target.getClass().getName(),
                    "Could not bind properties to " + targetClass,
                    ex.getCause() != null ? ex.getCause() : ex
            );
        }
    }

    private static BindHandler createBindHandler(ConfigurationProperties annotation) {
        // 使用默认的绑定处理器
        BindHandler handler = BindHandler.DEFAULT;

        if (annotation != null) {
            // 自定义处理器：忽略未知字段
            if (annotation.ignoreUnknownFields()) {
                handler = new IgnoreUnknownFieldsBindHandler(handler);
            }

            // 自定义处理器：忽略无效字段
            if (annotation.ignoreInvalidFields()) {
                handler = new IgnoreInvalidFieldsBindHandler(handler);
            }
        }
        return handler;
    }

    private static class IgnoreUnknownFieldsBindHandler implements BindHandler {
        private final BindHandler delegate;

        IgnoreUnknownFieldsBindHandler(BindHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object onFailure(
                ConfigurationPropertyName name,
                Bindable<?> target,
                BindContext context,
                Exception error
        ) throws Exception {
            if (error instanceof org.springframework.boot.context.properties.bind.UnboundConfigurationPropertiesException) {
                return null; // 忽略未知字段错误
            }
            return this.delegate.onFailure(name, target, context, error);
        }
    }

    // 忽略无效字段的处理器
    private static class IgnoreInvalidFieldsBindHandler implements BindHandler {
        private final BindHandler delegate;

        IgnoreInvalidFieldsBindHandler(BindHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object onFailure(
                ConfigurationPropertyName name,
                Bindable<?> target,
                BindContext context,
                Exception error
        ) throws Exception {
            if (error instanceof org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException) {
                return null; // 忽略无效值错误
            }
            return this.delegate.onFailure(name, target, context, error);
        }
    }


    public static int getInt(Properties properties, String key, int defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Long) {
            return ((Long) value).intValue();
        } else if (value instanceof String) {
            return StringTool.parseInt((String) value);
        }
        return defaultValue;
    }

    public static boolean getBoolean(Properties properties, String key, boolean defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            String str = (String) value;
            return ("1".equals(str) || "true".equalsIgnoreCase(str) || "Y".equalsIgnoreCase(str)
                    ? true
                    : false);
        }
        return defaultValue;
    }

    public static String getString(Properties properties, String key) {
        return getString(properties, key, "");
    }

    public static String getString(Properties properties, String key, String defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        } else if (value instanceof String) {
            return (String) value;
        } else {
            return String.valueOf(value);
        }
    }
}
