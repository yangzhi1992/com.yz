package com.commons.fuyoo.netty;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
public class NettyInitListener implements CommandLineRunner, ApplicationContextAware {

    @Value("${netty.port:18088}")
    private Integer nettyPort;

    private ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        try {
            new WebSocketApplication(nettyPort, applicationContext).start();
        } catch (Exception e) {
            System.out.println("NettyServerError:" + e.getMessage());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
