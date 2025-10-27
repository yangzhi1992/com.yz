package com.commons.spring.scope;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 每次创建新实例
 */
@Component
public class SimpleDateFormatExample {
    public String formatDate(Date date) {
        // 每次调用创建新实例，性能开销较大
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
