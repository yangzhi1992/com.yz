package com.commons.spring.scope;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 方案1：使用 ThreadLocal（推荐）
 */
@Component
public class ThreadSafeDateService {
    
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER =
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
    
    public String formatDate(Date date) {
        return DATE_FORMATTER.get().format(date);
    }
    
    // 如果是Web应用，需要在请求结束时清理ThreadLocal
    @PreDestroy
    public void cleanup() {
        DATE_FORMATTER.remove();
    }
}