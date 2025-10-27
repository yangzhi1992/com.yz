package com.commons.spring.scope;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 方案4：使用原型作用域 + 方法注入
 */
@Component
@Scope("singleton")
public class SingletonService {
    
    // 使用方法注入获取新的原型实例
    @Lookup
    protected NonThreadSafeBean createNonThreadSafeBean() {
        return null; // 由Spring实现
    }
    
    public void someMethod() {
        NonThreadSafeBean bean = createNonThreadSafeBean(); // 每次都是新实例
        // 使用bean...
    }
}

