package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * https://www.cnblogs.com/crazymakercircle/p/17436191.html#autoid-h4-7-2-1
 */
@SpringBootApplication(scanBasePackages = {
        "com.commons"
})
@EnableScheduling
public class Gateway2Application {
    public static void main(String[] args) {
        SpringApplication.run(Gateway2Application.class, args);
    }
}
