package com.commons.gateway;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

@Component
public class CustomDiscoveryClient implements DiscoveryClient {

    // 自定义服务实例列表
    private static final List<ServiceInstance> instancesA =
            Arrays.asList(
                    new CustomServiceInstance("0", "127.0.0.1", 8085)
            );

    private static final List<ServiceInstance> instancesB =
            Arrays.asList(
                    new CustomServiceInstance("1", "10.72.109.131", 8080),
                    new CustomServiceInstance("2", "10.132.174.122", 8080)
            );

    private static final List<ServiceInstance> instancesC =
            Arrays.asList(
                    new CustomServiceInstance("3", "10.72.158.178", 8080),
                    new CustomServiceInstance("4", "10.75.90.207", 8080),
                    new CustomServiceInstance("5", "10.75.90.204", 8080),
                    new CustomServiceInstance("6", "10.75.33.126", 8080)
            );

    Map<String, List<ServiceInstance>> serviceInstances = new ConcurrentHashMap<>();

    {
        serviceInstances.put("lb://routeA", instancesA);
        serviceInstances.put("lb://routeB", instancesA);
        serviceInstances.put("lb://routeC", instancesB);
    }

    /**
     * 返回当前支持的 DiscoveryClient 实现描述
     */
    @Override
    public String description() {
        return "Spring Cloud Gateway Dynamic Discovery Client";
    }

    /**
     * 获取所有已知的服务名称
     */
    @Override
    public List<String> getServices() {
        return new ArrayList<>(serviceInstances.keySet());
    }

    /**
     * 根据服务ID（服务名）获取所有可用的实例列表
     */
    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        List<ServiceInstance> instances = serviceInstances.get(serviceId);
        if (CollectionUtils.isEmpty(instances)) {
            return Collections.emptyList();
        }
        // 返回一个副本，避免外部操作修改内部数据
        return new ArrayList<>(instances);
    }

    /**
     * 【核心方法】更新指定服务的实例列表 可以通过 API、配置刷新事件等来调用此方法
     *
     * @param serviceId 服务ID
     * @param instances 新的实例列表
     */
    public void updateInstances(String serviceId, List<ServiceInstance> instances) {
        if (instances == null) {
            // 如果传入空列表，则移除该服务
            serviceInstances.remove(serviceId);
        } else {
            // 更新该服务的实例列表
            serviceInstances.put(serviceId, new ArrayList<>(instances));
        }
    }

    /**
     * 【核心方法】一次性更新所有服务映射
     *
     * @param newInstances 新的全量服务映射数据
     */
    public void updateAllInstances(Map<String, List<ServiceInstance>> newInstances) {
        this.serviceInstances.clear();
        this.serviceInstances.putAll(newInstances.entrySet()
                                                 .stream()
                                                 .collect(Collectors.toMap(
                                                         Map.Entry::getKey,
                                                         entry -> new ArrayList<>(entry.getValue()) // 同样进行拷贝
                                                 )));
    }

    /**
     * 自定义服务实例类
     */
    public static class CustomServiceInstance implements ServiceInstance {

        private final String serviceId;
        private final String host;
        private final int port;

        public CustomServiceInstance(String serviceId, String host, int port) {
            this.serviceId = serviceId;
            this.host = host;
            this.port = port;
        }

        @Override
        public String getServiceId() {
            return serviceId;
        }

        @Override
        public String getHost() {
            return host;
        }

        @Override
        public int getPort() {
            return port;
        }

        @Override
        public URI getUri() {
            return URI.create("http://" + host + ":" + port);
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public java.util.Map<String, String> getMetadata() {
            return java.util.Collections.emptyMap();
        }
    }
}
