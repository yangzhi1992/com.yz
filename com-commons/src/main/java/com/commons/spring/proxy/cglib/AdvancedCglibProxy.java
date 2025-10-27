package com.commons.spring.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 增强的CGLIB代理
public class AdvancedCglibProxy implements MethodInterceptor {
    
    private final Object target;
    private final List<CglibInterceptor> interceptors;
    
    public AdvancedCglibProxy(Object target, List<CglibInterceptor> interceptors) {
        this.target = target;
        this.interceptors = interceptors != null ? interceptors : new ArrayList<>();
    }
    
    public static <T> T createProxy(T target, CglibInterceptor... interceptors) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new AdvancedCglibProxy(target, Arrays.asList(interceptors)));
        return (T) enhancer.create();
    }
    
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // 执行前置拦截器
        for (CglibInterceptor interceptor : interceptors) {
            if (!interceptor.beforeInvoke(obj, method, args, proxy)) {
                System.out.println("【CGLIB拦截器】方法被拦截: " + method.getName());
                return null;
            }
        }
        
        Object result = null;
        Exception exception = null;
        
        try {
            // 执行目标方法
            result = proxy.invokeSuper(obj, args);
            return result;
            
        } catch (Exception e) {
            exception = e;
            throw e;
            
        } finally {
            // 执行后置拦截器
            for (CglibInterceptor interceptor : interceptors) {
                interceptor.afterInvoke(obj, method, args, result, exception, proxy);
            }
        }
    }
    
    // CGLIB拦截器接口
    public interface CglibInterceptor {
        default boolean beforeInvoke(Object obj, Method method, Object[] args, MethodProxy proxy) {
            return true;
        }
        
        default void afterInvoke(Object obj, Method method, Object[] args, Object result, 
                               Exception exception, MethodProxy proxy) {
        }
    }
}