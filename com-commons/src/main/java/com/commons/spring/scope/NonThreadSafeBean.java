package com.commons.spring.scope;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class NonThreadSafeBean {
    // 非线程安全的实现
}