package com.commons.spring.proxy;

import com.commons.spring.proxy.cglib.CglibProxy;
import com.commons.spring.proxy.cglib.UserServiceConcrete;
import com.commons.spring.proxy.jdk.JdkDynamicProxy;
import com.commons.spring.proxy.jdk.UserService;
import com.commons.spring.proxy.jdk.UserServiceImpl;
import net.sf.cglib.proxy.Proxy;

public class ProxyTest {
    
    public static void main(String[] args) {
        System.out.println("=== JDK动态代理测试 ===");
        testJdkProxy();
        
        /*System.out.println("\n=== CGLIB代理测试 ===");
        testCglibProxy();
        
        System.out.println("\n=== 性能对比测试 ===");
        performanceTest();*/
    }
    
    private static void testJdkProxy() {
        // 创建目标对象
        UserService target = new UserServiceImpl();
        
        // 创建JDK代理
        UserService proxy = JdkDynamicProxy.createProxy(target, UserService.class);
        
        // 测试代理方法
        proxy.addUser("张三");
        String user = proxy.getUser("李四");
        System.out.println("获取结果: " + user);
        proxy.deleteUser("王五");
        
        // 验证代理类型
        System.out.println("代理对象类型: " + proxy.getClass());
        System.out.println("是否是JDK代理: " + Proxy.isProxyClass(proxy.getClass()));
    }
    
    private static void testCglibProxy() {
        // 创建目标对象
        UserServiceConcrete target = new UserServiceConcrete();
        
        // 创建CGLIB代理
        UserServiceConcrete proxy = CglibProxy.createProxy(target);
        
        // 测试代理方法
        proxy.addUser("赵六");
        String user = proxy.getUser("钱七");
        System.out.println("获取结果: " + user);
        proxy.deleteUser("孙八");
        
        // 测试final方法（不会被代理拦截）
        proxy.finalMethod();
        
        // 验证代理类型
        System.out.println("代理对象类型: " + proxy.getClass());
        System.out.println("是否是CGLIB代理: " + proxy.getClass().getName().contains("$$EnhancerByCGLIB$$"));
    }
    
    private static void performanceTest() {
        int iterations = 10000;
        
        // JDK代理性能测试
        UserService jdkTarget = new UserServiceImpl();
        UserService jdkProxy = JdkDynamicProxy.createProxy(jdkTarget, UserService.class);
        
        long jdkStart = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            jdkProxy.getUser("test" + i);
        }
        long jdkEnd = System.currentTimeMillis();
        System.out.println("JDK代理 " + iterations + " 次调用耗时: " + (jdkEnd - jdkStart) + "ms");
        
        // CGLIB代理性能测试
        UserServiceConcrete cglibTarget = new UserServiceConcrete();
        UserServiceConcrete cglibProxy = CglibProxy.createProxy(cglibTarget);
        
        long cglibStart = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            cglibProxy.getUser("test" + i);
        }
        long cglibEnd = System.currentTimeMillis();
        System.out.println("CGLIB代理 " + iterations + " 次调用耗时: " + (cglibEnd - cglibStart) + "ms");
    }
}