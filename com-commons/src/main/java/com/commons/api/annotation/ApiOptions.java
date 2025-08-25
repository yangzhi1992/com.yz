package com.commons.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiOptions {

    /**
     * 连接超时,单位为毫秒
     */
    int connectTimeout() default -1;

    /**
     * 读超时,单位为毫秒
     */
    int readTimeout() default -1;

    /**
     * 写超时
     */
    int writeTimeout() default -1;

    /**
     * 重试次数,默认为1
     */
    int retries() default 1;

    /**
     * 连接超时配置项key,不支持动态刷新
     */
    String connectTimeoutKey() default "";

    /**
     * 读超时配置项key,不支持动态刷新
     */
    String readTimeoutKey() default "";

    /**
     * 写超时配置项key,不支持动态刷新
     */
    String writeTimeoutKey() default "";

    /**
     * 重试配置项key,不支持动态刷新
     * @return
     */
    String retriesKey() default "";
}
