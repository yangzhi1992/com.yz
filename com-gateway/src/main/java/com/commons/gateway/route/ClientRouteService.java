package com.commons.gateway.route;

import com.commons.gateway.serviceinstance.DiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableScheduling
public class ClientRouteService {
    public static final Map<String, List<ServiceInstance>> servicesMap = new ConcurrentHashMap<>();

    static {
        servicesMap.put("routea", Arrays.asList(
                new DiscoveryClient.CustomServiceInstance("1", "routea", "10.75.81.44", 8080, "/apis/admin/room/**")
        ));
        servicesMap.put("routec", Arrays.asList(
                new DiscoveryClient.CustomServiceInstance("2", "routec", "10.75.70.31", 8080, "/apis/msg/**"),
                new DiscoveryClient.CustomServiceInstance("3", "routec", "10.72.158.179", 8080, "/apis/msg/**"),
                new DiscoveryClient.CustomServiceInstance("4", "routec", "10.75.35.9", 8080, "/apis/msg/**"),
                new DiscoveryClient.CustomServiceInstance("5", "routec", "10.75.88.146", 8080, "/apis/msg/**")
        ));
    }

    @Scheduled(fixedRate = 5000)
    public void exec() {

    }
}
