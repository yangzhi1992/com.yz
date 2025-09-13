1、引用jar
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
        <version>${spring.boot.version}</version>
    </dependency>
2、配置文件
    actuator.yml
3、见有道云 !!springboot->actuator暴露的端点
4、动态日志级别调整
    /actuator/loggers 端点允许你实时查看和调整应用中日志的级别。
    查看日志级别：
        curl http://localhost:8080/actuator/loggers
    调整日志级别：
        curl -X POST -H "Content-Type: application/json" -d '{"configuredLevel": "DEBUG"}' http://localhost:8080/actuator/loggers/com.example.myapp
5、集成安全控制
    见security
6、使用 Prometheus 或 Grafana 监控
    引用jar 提供了将 Micrometer 的指标转换为 Prometheus 格式的能力
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
            <version>1.9.1</version>
        </dependency>
    启用 Prometheus,访问 /actuator/prometheus 可查看 Prometheus 格式的监控数据。
        management:
          endpoints:
            web:
              exposure:
                include: prometheus
7、高级配置
    自定义健康检查,需要实现 HealthIndicator 接口,通过 /actuator/health 端点查看健康状态