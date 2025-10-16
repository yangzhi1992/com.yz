package com.commons.jsonforjackson;

import com.commons.jsonforjackson.EnumEntity.OrderStatus;
import com.commons.jsonforjackson.EnumEntity.Priority;
import com.commons.jsonforjackson.EnumEntity.UserType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.text.SimpleDateFormat;
import java.time.*;
import java.math.BigDecimal;
import java.util.Date;

public class JsonFormatExample {
    
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper()
                // 美化输出
                .enable(SerializationFeature.INDENT_OUTPUT)
                // 允许单引号
                .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
                // 忽略未知属性
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 日期格式
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                // 注册Java8时间模块
                .registerModule(new JavaTimeModule())
                // 构造函数参数名支持
                .registerModule(new ParameterNamesModule())
                // JDK8其他特性支持;
                .registerModule(new Jdk8Module());
        
        // 注册 JavaTimeModule 以支持 Java 8 时间 API
        mapper.registerModule(new JavaTimeModule());
        
        // 禁用时间戳格式
        mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        testDateEntity(mapper);
        testJavaTimeEntity(mapper);
        testNumberEntity(mapper);
        testEnumEntity(mapper);
    }
    
    private static void testDateEntity(ObjectMapper mapper) throws Exception {
        System.out.println("=== DateEntity 测试 ===");

        DateEntity dateEntity = DateEntity.builder()
                .workStartTime(new Date())
                .updateTime(new Date())
                .birthday(new Date())
                .createTime(new Date())
                .timestamp(new Date())
                .build();
        
        String json = mapper.writeValueAsString(dateEntity);
        System.out.println("序列化结果: " + json);
        
        // 反序列化测试
        String jsonInput = "{\"createTime\":\"2023-10-01 14:30:00\",\"birthday\":\"2023-10-01\"," +
                          "\"workStartTime\":\"14:30:00\",\"timestamp\":1696141800000," +
                          "\"updateTime\":\"2023-10-01T06:30:00.000+00:00\"}";
        DateEntity deserialized = mapper.readValue(jsonInput, DateEntity.class);
        System.out.println("反序列化成功: " + deserialized.getCreateTime());
        System.out.println();
    }
    
    private static void testJavaTimeEntity(ObjectMapper mapper) throws Exception {
        System.out.println("=== JavaTimeEntity 测试 ===");
        
        JavaTimeEntity javaTimeEntity = new JavaTimeEntity(
            LocalDate.of(2023, 10, 1),
            LocalTime.of(14, 30, 0),
            LocalDateTime.of(2023, 10, 1, 14, 30, 0),
            ZonedDateTime.of(2023, 10, 1, 14, 30, 0, 0, ZoneId.of("Asia/Shanghai")),
            Instant.now(),
            YearMonth.of(2023, 10)
        );
        
        String json = mapper.writeValueAsString(javaTimeEntity);
        System.out.println("序列化结果: " + json);
        System.out.println();
    }
    
    private static void testNumberEntity(ObjectMapper mapper) throws Exception {
        System.out.println("=== NumberEntity 测试 ===");
        
        NumberEntity numberEntity = new NumberEntity(
            new BigDecimal("1234.56"),
            0.15,
            1234567.89,
            1400000000L,
            123
        );
        
        String json = mapper.writeValueAsString(numberEntity);
        System.out.println("序列化结果: " + json);
        System.out.println();
    }
    
    private static void testEnumEntity(ObjectMapper mapper) throws Exception {
        System.out.println("=== EnumEntity 测试 ===");
        
        EnumEntity enumEntity = new EnumEntity(
            OrderStatus.PROCESSING,
            UserType.ADMIN,
            Priority.HIGH
        );
        
        String json = mapper.writeValueAsString(enumEntity);
        System.out.println("序列化结果: " + json);
        
        // 反序列化测试
        String enumJson = "{\"orderStatus\":{\"description\":\"处理中\",\"code\":2}," +
                         "\"userType\":\"ADMIN\",\"priority\":2}";
        EnumEntity deserialized = mapper.readValue(enumJson, EnumEntity.class);
        System.out.println("反序列化成功: " + deserialized.getOrderStatus());
        System.out.println();
    }
}