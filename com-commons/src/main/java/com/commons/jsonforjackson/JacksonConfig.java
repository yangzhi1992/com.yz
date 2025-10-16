package com.commons.jsonforjackson;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {
    
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // 设置默认日期格式
            builder.dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            
            // 设置时区
            builder.timeZone(TimeZone.getTimeZone("GMT+8"));
            
            // Java 8 时间 API 支持
            builder.modules(new JavaTimeModule());
            
            // 禁用时间戳
            builder.featuresToDisable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            
            // 配置 LocalDate 格式
            builder.simpleDateFormat("yyyy-MM-dd");
            
            // 配置 LocalDateTime 格式
            builder.serializers(new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        };
    }
}