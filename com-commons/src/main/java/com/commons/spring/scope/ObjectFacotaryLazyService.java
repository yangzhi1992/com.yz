package com.commons.spring.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 方案5：使用ObjectFactory延迟获取
 */
@Component
public class ObjectFacotaryLazyService {
    @Autowired
    private ObjectFactory<NonThreadSafeBean> nonThreadSafeBeanFactory;

    public void someMethod() {
        NonThreadSafeBean bean = nonThreadSafeBeanFactory.getObject(); // 每次获取新实例
        // 使用bean...
    }
}
