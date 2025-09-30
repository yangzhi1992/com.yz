package com.commons.json;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

/**
 * // 替换Spring默认的Jackson
 */
@Configuration
public class JsonConfig {
    // Fastjson 安全防护指南：如何避免常见漏洞
    static {
        // 完全关闭 AutoType 功能
        ParserConfig.getGlobalInstance().setAutoTypeSupport(false);
        // 或者使用安全模式（推荐）
        ParserConfig.getGlobalInstance().setSafeMode(true);
    }


    // 替换Spring默认的Jackson
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        // 1. 创建Fastjson配置对象
        FastJsonConfig config = new FastJsonConfig();

        // 2. 配置序列化规则
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,          // 格式化输出
                SerializerFeature.WriteMapNullValue,     // 输出空字段
                SerializerFeature.WriteNullListAsEmpty,  // 空列表返回[]
                SerializerFeature.WriteNullStringAsEmpty, // 空字符串返回""
                SerializerFeature.DisableCircularReferenceDetect // 禁用循环引用检测
        );

        // 3. 设置日期格式
        config.setDateFormat("yyyy-MM-dd HH:mm:ss");

        // 4. 处理中文乱码
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        converter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_JSON,
                MediaType.TEXT_PLAIN
        ));

        return new HttpMessageConverters(converter);
    }
}
