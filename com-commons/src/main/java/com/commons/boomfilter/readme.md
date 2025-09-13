可以判断可能存在，但是一定不存在
1、单机
    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
        <version>31.1-jre</version> <!-- 版本请根据需要选择 -->
    </dependency>
2、redis 实现布隆过滤器
3、
    1. 缓存穿透防护（防击穿）
    2. 黑名单过滤器
    3. 搜索引擎爬虫去重
    4. 风控系统中的用户行为去重
    5. 数据库查询优化
    6. 分布式系统的分布式布隆过滤器
    7. 社交网络及机器学习推荐系统：社交网络中生成大量好友推荐，需确保推荐的好友用户是当前用户列表中没有的。


