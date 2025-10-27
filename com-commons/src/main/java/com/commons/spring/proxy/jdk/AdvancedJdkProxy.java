package com.commons.spring.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 增强的JDK代理处理器
public class AdvancedJdkProxy implements InvocationHandler {
    
    private final Object target;
    private final List<ProxyInterceptor> interceptors;
    
    public AdvancedJdkProxy(Object target, List<ProxyInterceptor> interceptors) {
        this.target = target;
        this.interceptors = interceptors != null ? interceptors : new ArrayList<>();
    }
    
    public static <T> T createProxy(T target, Class<T> interfaceClass, ProxyInterceptor... interceptors) {
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            new Class<?>[]{interfaceClass},
            new AdvancedJdkProxy(target, Arrays.asList(interceptors))
        );
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 执行前置拦截器
        for (ProxyInterceptor interceptor : interceptors) {
            if (!interceptor.beforeInvoke(proxy, method, args)) {
                System.out.println("【拦截器】方法被拦截: " + method.getName());
                return null;
            }
        }
        
        Object result = null;
        Exception exception = null;
        
        try {
            // 执行目标方法
            result = method.invoke(target, args);
            return result;
            
        } catch (Exception e) {
            exception = e;
            throw e.getCause();
            
        } finally {
            // 执行后置拦截器
            for (ProxyInterceptor interceptor : interceptors) {
                interceptor.afterInvoke(proxy, method, args, result, exception);
            }
        }
    }
    
    // 拦截器接口
    public interface ProxyInterceptor {
        default boolean beforeInvoke(Object proxy, Method method, Object[] args) {
            return true;
        }
        
        default void afterInvoke(Object proxy, Method method, Object[] args, Object result, Exception exception) {
        }
    }
}