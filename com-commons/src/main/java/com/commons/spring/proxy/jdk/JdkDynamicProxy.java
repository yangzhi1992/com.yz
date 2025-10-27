package com.commons.spring.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 3. 自定义调用处理器
public class JdkDynamicProxy implements InvocationHandler {
    
    private final Object target; // 目标对象
    
    public JdkDynamicProxy(Object target) {
        this.target = target;
    }
    
    /**
     * 创建代理对象
     */
    public static <T> T createProxy(T target, Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            new Class<?>[]{interfaceClass},
            new JdkDynamicProxy(target)
        );
    }
    
    /**
     * 通用的创建代理方法（基于目标对象的所有接口）
     */
    public static Object createProxy(Object target) {
        return Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            new JdkDynamicProxy(target)
        );
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 前置处理
        System.out.println("【JDK代理】开始执行方法: " + method.getName());
        System.out.println("【JDK代理】参数: " + (args != null ? String.join(", ", toString(args)) : "无"));
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 执行目标方法
            Object result = method.invoke(target, args);
            
            // 后置处理
            long endTime = System.currentTimeMillis();
            System.out.println("【JDK代理】方法执行成功，返回值: " + result);
            System.out.println("【JDK代理】执行耗时: " + (endTime - startTime) + "ms");
            
            return result;
            
        } catch (Exception e) {
            // 异常处理
            System.out.println("【JDK代理】方法执行异常: " + e.getCause().getMessage());
            throw e.getCause();
        } finally {
            System.out.println("【JDK代理】方法执行结束\n");
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