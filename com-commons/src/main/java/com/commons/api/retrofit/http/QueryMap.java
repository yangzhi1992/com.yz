
package com.commons.api.retrofit.http;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface QueryMap {

    boolean encoded() default false;
}
