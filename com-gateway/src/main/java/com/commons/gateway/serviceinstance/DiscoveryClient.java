package com.commons.gateway.serviceinstance;

import com.commons.gateway.route.ClientRouteService;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.*;

@Component
public class DiscoveryClient implements ReactiveDiscoveryClient {



    @Override
    public String description() {
        return "Custom Discovery Client";
    }

    @Override
    public Flux<ServiceInstance> getInstances(String serviceId) {
        return Flux.fromIterable(ClientRouteService.servicesMap.getOrDefault(serviceId, Collections.emptyList()));
    }
    @Override
    public Flux<String> getServices() {
        return Flux.fromIterable(new ArrayList<>(ClientRouteService.servicesMap.keySet()));
    }


    public static class CustomServiceInstance  implements ServiceInstance {
        private final String instanceId;
        private final String serviceId;
        private final String host;
        private final int port;
        private final String predicates;

        public CustomServiceInstance(String instanceId, String serviceId, String host, int port, String predicates) {
            this.instanceId = instanceId;
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

        @Override
        public String getInstanceId() {
            return instanceId;
        }
    }
}
