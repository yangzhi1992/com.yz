package com.commons.spring.proxy.jdk;

// 2. 实现类
public class UserServiceImpl implements UserService {
    @Override
    public void addUser(String username) {
        System.out.println("添加用户: " + username);
    }
    
    @Override
    public String getUser(String username) {
        System.out.println("获取用户: " + username);
        return "用户: " + username;
    }
    
    @Override
    public void deleteUser(String username) {
        System.out.println("删除用户: " + username);
    }
}