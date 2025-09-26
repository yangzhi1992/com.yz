package com.commons.gateway;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ServiceInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ServiceInitializer.class);
    private final RedisBackendService redisBackendService;

    public ServiceInitializer(
            RedisBackendService redisBackendService
    ) {
        this.redisBackendService = redisBackendService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Initializing backend services...");

        checkServicesHealth();
    }

    @Scheduled(fixedRate = 30000) // 每30秒检查一次
    public void checkServicesHealth() throws IOException {
        logger.info("Starting health check for all backend services");
//        exec(RedisBackendService.BACKEND_SERVICES_LIVECHAT_KEY);
//        exec(RedisBackendService.BACKEND_SERVICES_LIVECHAT_EXTERNAL_KEY);
        execPrometheus(RedisBackendService.BACKEND_SERVICES_PROMETHEUS_KEY);
    }

    private void execPrometheus(String key) throws IOException {
        List<String> list = Arrays.asList("10.69.132.250", "10.128.246.44");
        redisBackendService.clearAllServices(key)
                           .doOnSuccess(result -> logger.info("Cleared all services from Redis, result: {}", result))
                           .doOnError(
                                   error -> logger.error("Error clearing services from Redis: {}", error.getMessage()))
                           .then(Mono.defer(() -> {
                               AtomicInteger atomicInteger = new AtomicInteger();
                               return Flux.fromIterable(list)
                                          .flatMap(v -> {
                                              BackendServiceDTO service = new BackendServiceDTO();
                                              service.setId("service" + atomicInteger.incrementAndGet());
                                              service.setHost(v);
                                              service.setPort(9090);
                                              service.setHealthy(true);
                                              service.setKey(key);
                                              service.setPredicates(
                                                      Objects.equals(key,
                                                              RedisBackendService.BACKEND_SERVICES_LIVECHAT_EXTERNAL_KEY)
                                                              ? "/apis/msg" : "/**"
                                              );

                                              logger.info("Adding service: {} - {}:{}", service.getId(),
                                                      service.getHost(), service.getPort());

                                              return redisBackendService.addService(service)
                                                                        .doOnSuccess(count -> logger.info(
                                                                                "Successfully added service {}, result: {}",
                                                                                service.getId(), count))
                                                                        .doOnError(error -> logger.error(
                                                                                "Error adding service {}: {}",
                                                                                service.getId(), error.getMessage()));
                                          })
                                          .collectList()
                                          .then();
                           }))
                           .subscribe(
                                   unused -> logger.info("All services initialized successfully"),
                                   error -> logger.error("Error initializing services: {}", error.getMessage())
                           );
    }

}