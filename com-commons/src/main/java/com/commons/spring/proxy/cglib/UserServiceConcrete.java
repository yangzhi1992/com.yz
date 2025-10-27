package com.commons.spring.proxy.cglib;

// 目标类（不需要实现接口）
public class UserServiceConcrete {
    public void addUser(String username) {
        System.out.println("添加用户: " + username);
    }
    
    public String getUser(String username) {
        System.out.println("获取用户: " + username);
        return "用户: " + username;
    }
    
    public void deleteUser(String username) {
        System.out.println("删除用户: " + username);
    }
    
    // final方法 - CGLIB无法拦截
    public final void finalMethod() {
        System.out.println("这是final方法");
    }
    
    // private方法 - CGLIB无法拦截
    private void privateMethod() {
        System.out.println("这是private方法");
    }
}