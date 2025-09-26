package com.commons.gateway;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DynamicRouteService implements ApplicationEventPublisherAware {

    private final RouteDefinitionWriter routeDefinitionWriter;
    private final RouteDefinitionLocator routeDefinitionLocator;
    private final RedisBackendService redisBackendService;
    private ApplicationEventPublisher publisher;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public DynamicRouteService(RouteDefinitionWriter routeDefinitionWriter,
            RouteDefinitionLocator routeDefinitionLocator,
            RedisBackendService redisBackendService) {
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.routeDefinitionLocator = routeDefinitionLocator;
        this.redisBackendService = redisBackendService;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    @PostConstruct
    public void init() {
        // 初始加载路由
        refreshRoutes();

        // 定时检查后端服务变化
        scheduler.scheduleAtFixedRate(this::checkBackendServices, 0, 10, TimeUnit.SECONDS);
    }

    public void checkBackendServices() {
        redisBackendService.getAllServices(RedisBackendService.BACKEND_SERVICES_LIVECHAT_KEY)
                           .collectList()
                           .subscribe(services -> {
                               refreshRoutes();
                           });
        redisBackendService.getAllServices(RedisBackendService.BACKEND_SERVICES_LIVECHAT_EXTERNAL_KEY)
                           .collectList()
                           .subscribe(services -> {
                               refreshRoutes();
                           });
    }

    public void refreshRoutes() {
        // 删除所有动态路由
        routeDefinitionLocator.getRouteDefinitions()
                              .filter(route -> "dynamic_route".equals(route.getId()))
                              .flatMap(route -> routeDefinitionWriter.delete(Mono.just(route.getId())))
                              .then(Mono.defer(() -> {
                                  RouteDefinition definition = new RouteDefinition();
                                  definition.setId("dynamic_route");
                                  // 设置一个无效的URI，因为我们不会使用它
                                  definition.setUri(URI.create("http://invalid-backend:9999"));

                                  definition.setPredicates(Arrays.asList(
                                          new org.springframework.cloud.gateway.handler.predicate.PredicateDefinition(
                                                  "Path=/**")));

                                  return routeDefinitionWriter.save(Mono.just(definition));
                              }))
                              .then(Mono.defer(() -> {
                                  // 发布路由刷新事件
                                  publisher.publishEvent(new RefreshRoutesEvent(this));
                                  return Mono.empty();
                              }))
                              .subscribe(
                                      null,
                                      error -> System.err.println("Error refreshing routes: " + error.getMessage()),
                                      () -> System.out.println("Routes refreshed successfully")
                              );
    }
}