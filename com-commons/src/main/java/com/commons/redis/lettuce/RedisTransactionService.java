package com.commons.redis.lettuce;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class RedisTransactionService {

    private final RedisDataSourceManager dataSourceManager;

    public RedisTransactionService(RedisDataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    public void executeInTransaction(String dataSourceName, Runnable operations) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        template.setEnableTransactionSupport(true);

        template.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                try {
                    return operations.exec();
                } catch (Exception e) {
                    operations.discard();
                    throw e;
                }
            }
        });
    }
}