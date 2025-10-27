package com.commons.spring.scope;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方案3：使用线程安全的替代品
 */
@Component
public class SafeDateService {
    
    // 使用DateTimeFormatter（线程安全）
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public String formatDate(LocalDate date) {
        return FORMATTER.format(date);
    }
    
    // 对于集合，使用ConcurrentHashMap等线程安全容器
    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
}