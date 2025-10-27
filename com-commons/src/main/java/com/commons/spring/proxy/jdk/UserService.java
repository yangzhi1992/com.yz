package com.commons.spring.proxy.jdk;

// 1. 定义接口
public interface UserService {
    void addUser(String username);
    String getUser(String username);
    void deleteUser(String username);
}