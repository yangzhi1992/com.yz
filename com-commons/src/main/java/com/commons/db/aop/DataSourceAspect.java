package com.commons.db.aop;

import com.commons.db.DynamicDataSource;
import com.commons.db.annotation.DataSource;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class DataSourceAspect {

    @Pointcut("@annotation(com.commons.db.annotation.DataSource) || @within(com.commons.db.annotation.DataSource)")
    public void dataSourcePointCut() {
    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = point.getTarget().getClass();

        // 优先获取方法上的注解
        DataSource dataSource = method.getAnnotation(DataSource.class);

        // 如果方法上没有注解，则尝试获取类上的注解
        if (dataSource == null) {
            dataSource = targetClass.getAnnotation(DataSource.class);
        }

        if (dataSource != null) {
            // 设置数据源
            DynamicDataSource.setDataSourceKey(dataSource.value());
        }

        try {
            // 执行目标方法
            return point.proceed();
        } finally {
            // 清除数据源
            DynamicDataSource.clearDataSourceKey();
        }
    }

}