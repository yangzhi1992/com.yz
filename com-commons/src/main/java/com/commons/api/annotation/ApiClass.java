package com.commons.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiClass {

    String value();

    String baseUrl() default "";

    String requestInterceptor() default "defaultRequestInterceptor";

    String responseInterceptor() default "defaultResponseInterceptor";
}
