package com.commons.gateway.serviceinstance;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ServiceLoadBalancer implements ReactiveLoadBalancer<ServiceInstance> {
    
    private final DiscoveryClient serviceRegistry;
    private final Map<String, AtomicInteger> positionMap = new ConcurrentHashMap<>();
    
    public ServiceLoadBalancer(DiscoveryClient serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
    
    @Override
    public Mono<Response<ServiceInstance>> choose() {
        return choose(null);
    }
    
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        // 从请求中提取服务ID
        String serviceId = extractServiceId(request);
        
        if (serviceId == null) {
            return Mono.error(new IllegalArgumentException("Service ID not found in request"));
        }
        
        return serviceRegistry.getInstances(serviceId)
            .collectList()
            .flatMap(instances -> {
                if (instances.isEmpty()) {
                    return Mono.error(new IllegalStateException("No available instances for service: " + serviceId));
                }
                
                // 获取或创建该服务的轮询计数器
                AtomicInteger position = positionMap.computeIfAbsent(
                    serviceId, k -> new AtomicInteger(0)
                );
                
                // 使用轮询算法选择一个实例
                int index = position.getAndIncrement() % instances.size();
                if (position.get() > 10000) {
                    position.set(0);
                }
                
                ServiceInstance selectedInstance = instances.get(index);
                
                return Mono.just(new DefaultResponse(selectedInstance));
            });
    }
    
    private String extractServiceId(Request request) {
        // 根据您的请求对象结构提取服务ID
        // 这取决于您如何传递服务ID信息
        if (request != null && request.getContext() != null) {
            Object serviceIdObj = request.getContext();
            if (serviceIdObj instanceof String) {
                return (String) serviceIdObj;
            }
        }
        return null;
    }
}