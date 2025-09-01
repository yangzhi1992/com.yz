package com.commons.limiter;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class ExternalApiService {

    private final Random random = new Random();
    
    // 模拟可能失败的外部API调用
    public String callExternalApi(String param) {
        // 模拟不同的响应场景
        int scenario = random.nextInt(100);
        
        if (scenario < 20) { // 20% 概率失败
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "External API error");
        } else if (scenario < 40) { // 20% 概率慢响应
            try {
                TimeUnit.SECONDS.sleep(3); // 慢响应
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted", e);
            }
        } else if (scenario < 45) { // 5% 概率超时
            try {
                TimeUnit.SECONDS.sleep(5); // 超过配置的超时时间
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted", e);
            }
        }
        
        return "Response for: " + param;
    }
    
    // 模拟获取用户信息的方法，用于缓存演示
    public String getUserInfo(String userId) {
        // 模拟数据库查询或外部API调用
        try {
            TimeUnit.MILLISECONDS.sleep(100); // 模拟网络延迟
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return "User info for: " + userId;
    }
}