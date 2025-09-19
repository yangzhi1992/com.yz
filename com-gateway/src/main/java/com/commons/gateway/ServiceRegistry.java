package com.commons.gateway;

import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceRegistry {

    public static final Map<String, List<ServiceInstance>> servicesMap = new ConcurrentHashMap<>();

    static {
        // 自定义服务实例列表
        servicesMap.put("routec", Arrays.asList(
            new ServiceInstance("routec", "http://10.75.70.31:8080"),
            new ServiceInstance("routec", "http://10.72.158.179:8080"),
            new ServiceInstance("routec", "http://10.75.35.9:8080"),
            new ServiceInstance("routec", "http://10.75.88.146:8080")
        ));
    }

    public static List<ServiceInstance> getInstances(String serviceId) {
        return servicesMap.getOrDefault(serviceId, Collections.emptyList());
    }

    public static class ServiceInstance {
        private final String serviceId;
        private final String uri;

        public ServiceInstance(String serviceId, String uri) {
            this.serviceId = serviceId;
            this.uri = uri;
        }

        public String getServiceId() {
            return serviceId;
        }

        public URI getUri() {
            return URI.create(uri);
        }
    }
}
