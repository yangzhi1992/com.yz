package com.commons.spring.proxy;

import com.commons.spring.proxy.cglib.CglibProxy;
import com.commons.spring.proxy.cglib.UserServiceConcrete;
import com.commons.spring.proxy.jdk.JdkDynamicProxy;
import com.commons.spring.proxy.jdk.UserService;
import com.commons.spring.proxy.jdk.UserServiceImpl;

// 统一的代理工厂
public class ProxyFactory {
    
    /**
     * 创建代理对象（自动选择代理方式）
     */
    public static <T> T createProxy(T target) {
        if (target == null) {
            throw new IllegalArgumentException("目标对象不能为null");
        }
        
        Class<?> targetClass = target.getClass();
        
        // 如果有接口，使用JDK动态代理
        if (targetClass.getInterfaces().length > 0) {
            System.out.println("使用JDK动态代理为 " + targetClass.getName() + " 创建代理");
            return (T) JdkDynamicProxy.createProxy(target);
        } 
        // 否则使用CGLIB代理
        else {
            System.out.println("使用CGLIB代理为 " + targetClass.getName() + " 创建代理");
            return (T) CglibProxy.createProxy(target);
        }
    }
    
    /**
     * 强制使用CGLIB代理
     */
    public static <T> T createCglibProxy(T target) {
        return CglibProxy.createProxy(target);
    }
    
    /**
     * 强制使用JDK代理（必须实现接口）
     */
    public static <T> T createJdkProxy(T target, Class<T> interfaceClass) {
        if (!interfaceClass.isInstance(target)) {
            throw new IllegalArgumentException("目标对象必须实现接口: " + interfaceClass.getName());
        }
        return JdkDynamicProxy.createProxy(target, interfaceClass);
    }
}

