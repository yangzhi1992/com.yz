package com.commons.spring.proxy;

import com.commons.spring.proxy.cglib.AdvancedCglibProxy;
import com.commons.spring.proxy.cglib.UserServiceConcrete;
import com.commons.spring.proxy.jdk.AdvancedJdkProxy;
import com.commons.spring.proxy.jdk.UserService;
import com.commons.spring.proxy.jdk.UserServiceImpl;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class AdvancedProxyTest {
    
    public static void main(String[] args) {
        testAdvancedJdkProxy();
        testAdvancedCglibProxy();
    }
    
    private static void testAdvancedJdkProxy() {
        UserService target = new UserServiceImpl();
        
        // 创建带拦截器的JDK代理
        UserService proxy = AdvancedJdkProxy.createProxy(target, UserService.class,
            new AdvancedJdkProxy.ProxyInterceptor() {
                @Override
                public boolean beforeInvoke(Object proxy, Method method, Object[] args) {
                    if ("deleteUser".equals(method.getName())) {
                        System.out.println("【安全拦截】删除操作需要权限验证");
                        // 模拟权限检查
                        return checkPermission();
                    }
                    return true;
                }
                
                @Override
                public void afterInvoke(Object proxy, Method method, Object[] args, Object result, Exception exception) {
                    if (exception == null) {
                        System.out.println("【日志记录】方法 " + method.getName() + " 执行成功");
                    } else {
                        System.out.println("【错误记录】方法 " + method.getName() + " 执行失败: " + exception.getMessage());
                    }
                }
                
                private boolean checkPermission() {
                    // 模拟权限检查
                    return Math.random() > 0.5;
                }
            }
        );
        
        proxy.addUser("高级用户");
        proxy.deleteUser("测试用户"); // 可能被拦截
    }
    
    private static void testAdvancedCglibProxy() {
        UserServiceConcrete target = new UserServiceConcrete();
        
        // 创建带拦截器的CGLIB代理
        UserServiceConcrete proxy = AdvancedCglibProxy.createProxy(target,
            new AdvancedCglibProxy.CglibInterceptor() {
                @Override
                public boolean beforeInvoke(Object obj, Method method, Object[] args, MethodProxy proxy) {
                    System.out.println("【CGLIB前置】准备执行: " + method.getName());
                    if (method.getName().startsWith("delete")) {
                        System.out.println("【危险操作警告】执行删除操作");
                    }
                    return true;
                }
                
                @Override
                public void afterInvoke(Object obj, Method method, Object[] args, Object result, 
                                      Exception exception, MethodProxy proxy) {
                    if (exception != null) {
                        System.out.println("【CGLIB异常】方法执行异常: " + exception.getMessage());
                    } else {
                        System.out.println("【CGLIB后置】方法执行完成");
                    }
                }
            }
        );
        
        proxy.addUser("CGLIB用户");
        proxy.deleteUser("CGLIB测试用户");
    }
}