package com.commons.test.hibernateValidator;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated // 启用方法级验证
public class UserService {

    public User getUserById(@Min(1) Long id) {
        return (User)User.builder().build();
    }
    
    public void createUser(@Valid User user) {
        // 业务逻辑
    }
}
