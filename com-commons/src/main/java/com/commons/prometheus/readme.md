prometheus 收集指标 ， 及PromQL语句
    https://prometheus.io/download/ 下载
    https://prometheus.io/docs/alerting/latest/alertmanager/ 报警
    https://prometheus.io/docs/instrumenting/exporters/ exoirters
    http://localhost:9090/ prometheus启动后prometheus管理页面
    grafana 展示指标    

altermanager prometheus指标任务报警
prometheus_gateway prometheus指标采集网关

1、spring boot暴露指标是prometheus指标
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
2、引用java jar
    <!-- The prometheus client begin-->
    <!-- Prometheus 核心客户端库 -->
    <dependency>
        <groupId>io.prometheus</groupId>
        <artifactId>simpleclient</artifactId>
        <version>0.16.0</version>
    </dependency>
    <!-- Hotspot JVM 指标收集器 -->
    <dependency>
        <groupId>io.prometheus</groupId>
        <artifactId>simpleclient_hotspot</artifactId>
        <version>0.16.0</version>
    </dependency>
    <!-- Prometheus HTTPServer 用于暴露指标 -->
    <dependency>
        <groupId>io.prometheus</groupId>
        <artifactId>simpleclient_httpserver</artifactId>
        <version>0.16.0</version>
    </dependency>
    <!-- 推送数据到 pushgateway -->
    <dependency>
        <groupId>io.prometheus</groupId>
        <artifactId>simpleclient_pushgateway</artifactId>
        <version>0.16.0</version>
    </dependency>
    <!-- The prometheus client end-->
3、