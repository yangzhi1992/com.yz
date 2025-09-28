package com.commons.redis.lettuce;

import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class BusinessService {

    private final MultiRedisService multiRedisService;

    public BusinessService(MultiRedisService multiRedisService) {
        this.multiRedisService = multiRedisService;
    }

    public void cacheUserInfo(String name) {
        // 使用主数据源缓存用户信息
        multiRedisService.set("primary", "user:" + name, name, 30, TimeUnit.MINUTES);
    }
}