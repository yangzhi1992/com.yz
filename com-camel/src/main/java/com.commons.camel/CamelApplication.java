package com.commons.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CamelApplication {
    public static void main(String[] args) {
        SpringApplication.run(CamelApplication.class, args);
    }

    // 定义 Camel 路由
    static class MyRoute extends RouteBuilder {
        @Override
        public void configure() {
            from("timer:hello?period=1000")
                .setBody().constant("Hello World!")
                .to("stream:out"); // 输出到控制台
        }
    }
}
