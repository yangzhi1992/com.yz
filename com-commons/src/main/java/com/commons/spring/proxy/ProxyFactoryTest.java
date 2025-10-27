package com.commons.spring.proxy;

import com.commons.spring.proxy.cglib.UserServiceConcrete;
import com.commons.spring.proxy.jdk.UserService;
import com.commons.spring.proxy.jdk.UserServiceImpl;

// 使用示例
public class ProxyFactoryTest {
    public static void main(String[] args) {
        // 自动选择代理方式
        UserService userService = ProxyFactory.createProxy(new UserServiceImpl());
        userService.addUser("工厂创建的用户");
        
        UserServiceConcrete concreteService = ProxyFactory.createProxy(new UserServiceConcrete());
        concreteService.addUser("工厂创建的CGLIB用户");
        
        // 强制使用指定代理方式
        UserService forcedJdkProxy = ProxyFactory.createJdkProxy(new UserServiceImpl(), UserService.class);
        forcedJdkProxy.getUser("强制JDK代理");
        
        UserServiceConcrete forcedCglibProxy = ProxyFactory.createCglibProxy(new UserServiceConcrete());
        forcedCglibProxy.getUser("强制CGLIB代理");
    }
}