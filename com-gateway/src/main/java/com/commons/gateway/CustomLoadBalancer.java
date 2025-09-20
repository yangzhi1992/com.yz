package com.commons.gateway;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import reactor.core.publisher.Mono;

import java.util.Random;

public class CustomLoadBalancer implements ReactiveLoadBalancer<ServiceInstance> {

    private String serviceId;
    private final ServiceRegistry serviceRegistry;
    private final Random random = new Random();

    public CustomLoadBalancer(String serviceId, ServiceRegistry serviceRegistry) {
        this.serviceId = serviceId;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        return serviceRegistry.getInstances(serviceId)
                .collectList()
                .flatMap(instances -> {
                    if (instances.isEmpty()) {
                        return Mono.error(new IllegalStateException("No available instances for service: " + serviceId));
                    }

                    // 使用负载均衡算法选择一个实例
                    ServiceInstance selectedInstance = instances.get(random.nextInt(instances.size()));

                    return Mono.just(new DefaultResponse(new DefaultServiceInstance(
                            selectedInstance.getInstanceId(),
                            selectedInstance.getServiceId(),
                            selectedInstance.getHost(),
                            selectedInstance.getPort(),
                            false
                    )));
                });
    }
}
