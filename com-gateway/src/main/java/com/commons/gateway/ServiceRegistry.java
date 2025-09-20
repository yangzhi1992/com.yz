package com.commons.gateway;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServiceRegistry implements ReactiveDiscoveryClient {

    public static final Map<String, List<ServiceInstance>> servicesMap = new ConcurrentHashMap<>();

    static {
        servicesMap.put("routea", Arrays.asList(
                new CustomServiceInstance("routea", "10.75.81.44",8080,"/apis/admin/room/**")
        ));
        servicesMap.put("routec", Arrays.asList(
            new CustomServiceInstance("routec", "10.75.70.31",8080,"/apis/msg/**"),
            new CustomServiceInstance("routec", "10.72.158.179",8080,"/apis/msg/**"),
            new CustomServiceInstance("routec", "10.75.35.9",8080,"/apis/msg/**"),
            new CustomServiceInstance("routec", "10.75.88.146",8080,"/apis/msg/**")
        ));
    }

    @Override
    public String description() {
        return "Custom Discovery Client";
    }

    @Override
    public Flux<ServiceInstance> getInstances(String serviceId) {
        return Flux.fromIterable(servicesMap.getOrDefault(serviceId, Collections.emptyList()));
    }
    @Override
    public Flux<String> getServices() {
        return Flux.fromIterable(new ArrayList<>(servicesMap.keySet()));
    }


    public static class CustomServiceInstance  implements ServiceInstance {
        private final String serviceId;
        private final String host;
        private final int port;
        private final String predicates;

        public CustomServiceInstance(String serviceId, String host, int port, String predicates) {
            this.serviceId = serviceId;
            this.host = host;
            this.port = port;
            this.predicates = predicates;
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
        public boolean isSecure() {
            return false;
        }

        @Override
        public URI getUri() {
            return URI.create(String.format("http://%s:%d", host, port));
        }

        @Override
        public Map<String, String> getMetadata() {
            Map<String,String> map = new HashMap<>();
            map.put("weight","1");
            return map; // 默认权重为1
        }

        public String getPredicates() {
            return predicates;
        }
    }
}
