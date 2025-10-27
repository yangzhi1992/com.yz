package com.commons.spring.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

// CGLIB代理工厂
public class CglibProxy implements MethodInterceptor {
    
    private final Object target;
    
    public CglibProxy(Object target) {
        this.target = target;
    }
    
    /**
     * 创建代理对象
     */
    public static <T> T createProxy(T target) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new CglibProxy(target));
        return (T) enhancer.create();
    }
    
    /**
     * 创建代理对象（指定回调）
     */
    public static <T> T createProxy(Class<T> targetClass, MethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(interceptor);
        return (T) enhancer.create();
    }
    
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // 前置处理
        System.out.println("【CGLIB代理】开始执行方法: " + method.getName());
        System.out.println("【CGLIB代理】参数: " + (args != null ? String.join(", ", toString(args)) : "无"));
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 执行目标方法 - 注意：使用invokeSuper调用原始方法
            Object result = proxy.invokeSuper(obj, args);
            
            // 后置处理
            long endTime = System.currentTimeMillis();
            System.out.println("【CGLIB代理】方法执行成功，返回值: " + result);
            System.out.println("【CGLIB代理】执行耗时: " + (endTime - startTime) + "ms");
            
            return result;
            
        } catch (Exception e) {
            // 异常处理
            System.out.println("【CGLIB代理】方法执行异常: " + e.getMessage());
            throw e;
        } finally {
            System.out.println("【CGLIB代理】方法执行结束\n");
        }
    }
    
    private String[] toString(Object[] args) {
        if (args == null) return new String[0];
        String[] result = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = String.valueOf(args[i]);
        }
        return result;
    }
}