package com.commons.redis.lettuce;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

@Service
public class RedisOperationService {

    private final RedisDataSourceManager dataSourceManager;

    public RedisOperationService(RedisDataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    /**
     * 在事务中执行 Redis 操作
     *
     * @param dataSourceName 数据源名称
     * @param operations 要执行的操作（接收 RedisOperations 参数）
     * @return 事务执行结果列表
     */
    public List<Object> executeInTransaction(String dataSourceName,
            Consumer<RedisOperations<String, Object>> operations) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        template.setEnableTransactionSupport(true);

        return template.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations ops) throws DataAccessException {
                try {
                    ops.multi();
                    operations.accept(ops); // 执行外部传入的操作
                    return ops.exec();
                } catch (Exception e) {
                    ops.discard();
                    throw e;
                }
            }
        });
    }

    /**
     * 使用 Pipeline 执行 Redis 操作
     *
     * @param dataSourceName 数据源名称
     * @param operations 要执行的操作（接收 RedisConnection 参数）
     * @return Pipeline 执行结果列表
     */
    public List<Object> executeWithPipeline(String dataSourceName, Consumer<RedisConnection> operations) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        return template.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                operations.accept(connection); // 执行外部传入的操作
                return null;
            }
        });
    }

    /**
     * 使用 Pipeline 执行 RedisTemplate 操作（更高级的接口）
     *
     * @param dataSourceName 数据源名称
     * @param operations 要执行的操作（接收 RedisTemplate 参数）
     * @return Pipeline 执行结果列表
     */
    public List<Object> executeWithPipelineTemplate(String dataSourceName,
            Consumer<RedisTemplate<String, Object>> operations) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        return template.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations ops) throws DataAccessException {
                operations.accept(template); // 执行外部传入的操作
                return null;
            }
        });
    }

    // 使用事务 事务操作（RedisOperations）
    public void transactionalExample() {
        List<Object> results = this.executeInTransaction("defaultDataSource", ops -> {
            ops.opsForValue()
               .set("user:1", "张三");
            ops.opsForHash()
               .put("user:details:1", "age", "30");
            ops.opsForSet()
               .add("user:roles:1", "admin", "editor");
        });

        System.out.println("事务执行结果: " + results);
    }

    // 使用Pipeline（RedisConnection接口） Pipeline 基础操作（RedisConnection）
    public void pipelineBasicExample() {
        List<Object> results = this.executeWithPipeline("defaultDataSource", connection -> {
            connection.set("config:timeout".getBytes(), "30".getBytes());
            connection.set("config:max_connections".getBytes(), "100".getBytes());
            connection.get("config:timeout".getBytes());
        });

        System.out.println("Pipeline执行结果: " + results);
    }

    // 使用Pipeline（RedisTemplate接口） Pipeline 高级操作（RedisTemplate）
    public void pipelineAdvancedExample() {
        List<Object> results = this.executeWithPipelineTemplate("defaultDataSource", template -> {
            template.opsForValue()
                    .set("product:1", "手机");
            template.opsForZSet()
                    .add("product:ranking", "product:1", 100);
        });

        System.out.println("Pipeline执行结果: " + results);
    }

    //->executeInTransaction
    public void executeInTransactionExample(String dataSourceName) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        template.setEnableTransactionSupport(true);

        template.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                try {
                    operations.multi();
                    operations.opsForValue()
                              .set("user:1", "1");
                    operations.opsForValue()
                              .set("user:1", "1");
                    return operations.exec();
                } catch (Exception e) {
                    operations.discard();
                    throw e;
                }
            }
        });
    }

    //->executeWithPipeline
    public void executeWithPipelineExample(String dataSourceName) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        template.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set("key1".getBytes(), "value1".getBytes());
                connection.set("key2".getBytes(), "value2".getBytes());
                connection.get("key1".getBytes());
                return null; // 实际返回值由executePipelined处理
            }
        });
    }

    //->executeWithPipelineTemplate
    public void executeWithPipelineExample2(String dataSourceName) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        template.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.opsForValue()
                          .get("key");
                operations.opsForValue()
                          .set((K)"key", (V)"value");
                return null;
            }
        });
    }

    /**
     * 执行 Lua 脚本
     *
     * @param dataSourceName 数据源名称
     * @param script Lua 脚本内容
     * @param keys 脚本中使用的 KEYS 列表
     * @param args 脚本中使用的 ARGV 参数
     * @return 脚本执行结果
     */
    public <T> T executeLuaScript(String dataSourceName, String script, List<String> keys, Object... args) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        RedisScript<T> redisScript = new DefaultRedisScript<>(script);
        return template.execute(redisScript, keys, args);
    }

    /**
     * 执行预加载的 Lua 脚本
     *
     * @param dataSourceName 数据源名称
     * @param scriptSha 脚本的 SHA1 校验和
     * @param resultType 返回结果类型
     * @param keys 脚本中使用的 KEYS 列表
     * @param args 脚本中使用的 ARGV 参数
     * @return 脚本执行结果
     */
    public <T> T executeLuaScriptBySha(String dataSourceName, String scriptSha, Class<T> resultType,
            List<String> keys, Object... args) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        RedisScript<T> redisScript = new DefaultRedisScript<>(scriptSha, resultType);
        return template.execute(redisScript, keys, args);
    }

    /**
     * 加载 Lua 脚本到 Redis 服务器
     *
     * @param dataSourceName 数据源名称
     * @param script Lua 脚本内容
     * @return 脚本的 SHA1 校验和
     */
    public String loadLuaScript(String dataSourceName, String script) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);
        return template.getConnectionFactory()
                       .getConnection()
                       .scriptLoad(script.getBytes());
    }

    /**
     * 在集群环境下执行 Lua 脚本(确保所有 key 在同一个 slot)
     *
     * @param dataSourceName 数据源名称
     * @param script Lua 脚本
     * @param keys 必须使用 hash tag 确保在同一个 slot
     * @param args 参数
     * @return 执行结果
     */
    public <T> T executeClusterLuaScript(String dataSourceName, String script,
            List<String> keys, Object... args) {
        RedisTemplate<String, Object> template = dataSourceManager.getRedisTemplate(dataSourceName);

        // 验证所有 key 是否使用相同的 hash tag
        if (!isSameSlot(keys)) {
            throw new IllegalArgumentException("集群模式下所有KEY必须使用相同的hash tag");
        }

        RedisScript<T> redisScript = new DefaultRedisScript<>(script);
        return template.execute(redisScript, keys, args);
    }

    private boolean isSameSlot(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return true;
        }

        String firstTag = extractHashTag(keys.get(0));
        if (firstTag == null) {
            return false;
        }

        for (String key : keys) {
            if (!firstTag.equals(extractHashTag(key))) {
                return false;
            }
        }
        return true;
    }

    private String extractHashTag(String key) {
        if (key == null) {
            return null;
        }

        int start = key.indexOf('{');
        if (start == -1) {
            return null;
        }

        int end = key.indexOf('}', start + 1);
        if (end == -1) {
            return null;
        }

        return key.substring(start + 1, end);
    }

    // 执行简单Lua脚本
    public void example1() {
        String script = "return redis.call('GET', KEYS[1])";
        String result = this.executeLuaScript(
                "defaultDataSource",
                script,
                Collections.singletonList("myKey"),
                new Object[0]
        );
        System.out.println("Result: " + result);
    }

    // 预加载并执行脚本
    public void example2() {
        String script = "return {KEYS[1], ARGV[1]}";
        String sha = this.loadLuaScript("defaultDataSource", script);

        List<String> result = this.executeLuaScriptBySha(
                "defaultDataSource",
                sha,
                List.class,
                Collections.singletonList("key1"),
                "arg1"
        );
        System.out.println("Result: " + result);
    }

    // 集群环境下使用hash tag确保key在同一个slot
    public void example3() {
        String script =
                "redis.call('HSET', KEYS[1], 'name', ARGV[1])\n" +
                        "redis.call('HSET', KEYS[1], 'age', ARGV[2])\n" +
                        "return redis.call('HGETALL', KEYS[1])";

        Map<String, String> result = this.executeClusterLuaScript(
                "clusterDataSource",
                script,
                Collections.singletonList("user:{1001}"), // 注意hash tag
                "张三", "30"
        );
        System.out.println("用户信息: " + result);
    }
}
