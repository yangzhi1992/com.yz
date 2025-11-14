package com.commons.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CamelSpringApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(CamelSpringApplication.class, args);
        //spring main启动后保持系统常驻运行
        //1、Thread.currentThread().join(); 线程阻塞
        //2、在application.yml中配置camel.springboot.main-run-controller=true-为了保持 Spring Boot 应用程序和 Apache Camel 的主线程持续运行（阻塞运行）
        //3、引入第三方常驻jar 这个表示应用是http常驻服务<dependency>
        //    <groupId>org.springframework.boot</groupId>
        //    <artifactId>spring-boot-starter-web</artifactId>
        //</dependency>
        Thread.currentThread().join();
    }

    @Bean
    RouteBuilder timerRouter() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("timer:hello?period=1000000") // 定时器每1000秒触发一次
                        .setBody().constant("Timer Hello World!") // 设置消息体
                        .to("stream:out"); // 输出到控制台 camel-stream
            }
        };
    }

    @Bean
    RouteBuilder quartzRouter() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("quartz://myQuartzScheduler?cron=0/10+*+*+*+*+?") // 定时器每秒触发一次 camel-quartz
                        .setBody().constant("Quartz Hello World!") // 设置消息体
                        .to("stream:out"); // 输出到控制台 camel-stream
            }
        };
    }
}
