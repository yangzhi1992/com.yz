package com.commons.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RedisBackendService {

    public static final String BACKEND_SERVICES_LIVECHAT_KEY = "BACKEND_SERVICES_LIVECHAT";
    public static final String BACKEND_SERVICES_LIVECHAT_EXTERNAL_KEY = "BACKEND_SERVICES_LIVECHAT_EXTERNAL";
    public static final String BACKEND_SERVICES_PROMETHEUS_KEY = "BACKEND_SERVICES_PROMETHEUS_KEY";

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisBackendService(ReactiveRedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public Flux<BackendServiceDTO> getAllServices(String key) {
        return redisTemplate.opsForList()
                            .range(key, 0, -1)
                            .flatMap(obj -> {
                                try {
                                    String json = objectMapper.writeValueAsString(obj);
                                    BackendServiceDTO service = objectMapper.readValue(json, BackendServiceDTO.class);
                                    return Mono.just(service);
                                } catch (Exception e) {
                                    return Mono.empty();
                                }
                            });
    }

    public Mono<Long> addService(BackendServiceDTO service) {
        return redisTemplate.opsForList()
                            .rightPush(service.getKey(), service);
    }

    public Mono<Long> removeService(String serviceId, String key) {
        return getAllServices(key)
                .filter(service -> serviceId.equals(service.getId()))
                .next()
                .flatMap(service ->
                        redisTemplate.opsForList()
                                     .remove(key, 1, service))
                .defaultIfEmpty(0L);
    }

    public Mono<Boolean> updateService(BackendServiceDTO updatedService) {
        return getAllServices(updatedService.getKey())
                .filter(service -> updatedService.getId()
                                                 .equals(service.getId()))
                .next()
                .flatMap(existingService ->
                        removeService(existingService.getId(), updatedService.getKey())
                                .then(addService(updatedService))
                                .map(count -> count > 0))
                .defaultIfEmpty(false);
    }

    public Mono<Boolean> clearAllServices(String key) {
        return redisTemplate.delete(key)
                            .hasElement();
    }
}