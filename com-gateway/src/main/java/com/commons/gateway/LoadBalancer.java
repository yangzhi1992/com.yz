package com.commons.gateway;

import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancer {

    private final AtomicInteger position = new AtomicInteger(0);

    /**
     * 轮询策略：从实例列表中选出下一个服务实例
     */
    public URI choose(String serviceId) {
        List<ServiceRegistry.ServiceInstance> instances = ServiceRegistry.getInstances(serviceId);

        if (instances.isEmpty()) {
            throw new IllegalStateException("No available instances for service: " + serviceId);
        }

        // 基于轮询策略选择实例
        int pos = Math.abs(position.incrementAndGet()) % instances.size();
        return instances.get(pos).getUri();
    }
}
