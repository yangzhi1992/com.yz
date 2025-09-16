1、ConcurrentHashMap
2、Caffeine
    简介：当今 Java 本地缓存领域的王者，是 Guava Cache 的“现代化”重构版，性能极其优异，API 友好，功能丰富。
    核心特性：
    高性能：使用 Window-TinyLFU 淘汰算法，提供了近乎最佳的命中率，尤其擅长处理突发性的稀疏流量（突然的大量访问）。其读写性能在众多框架中名列前茅。
    丰富的API：提供灵活的手动加载、异步加载、异步刷新机制。
    自动刷新：支持在访问时异步自动刷新缓存项（refreshAfterWrite），避免在刷新时阻塞请求。
    事件监听：支持缓存移除事件监听器（removalListener）。
    完善的统计：内置强大的统计功能，可轻松获取命中率等信息。
    与 Spring 完美集成：Spring Boot 2.x 后已将默认缓存实现从 Guava Cache 切换为 Caffeine。
2.1 引用cacffeinejar
    <dependency>
        <groupId>com.github.ben-manes.caffeine</groupId>
        <artifactId>caffeine</artifactId>
        <version>3.1.8</version>
    </dependency>
2.2 集成spring-boot引入jar
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>
2.3 若需要统计引入
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <!-- prometheus格式统计 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
# 配置指标
management:
    endpoints:
        web:
            exposure:
                include: health,info,metrics,caches,beans
    endpoint:
        health:
            show-details: always
# 指标导出配置（如果使用Prometheus）
management:
    metrics:
        export:
            prometheus:
                enabled: true
        distribution:
            percentiles-histogram:
                http.server.requests: true
        cache:
            caffeine:
                    stats: true
   