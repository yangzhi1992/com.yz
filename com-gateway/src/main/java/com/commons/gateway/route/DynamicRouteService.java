package com.commons.gateway.route;

import com.commons.gateway.serviceinstance.DiscoveryClient;
import com.commons.gateway.serviceinstance.ServiceLoadBalancer;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@EnableScheduling
public class DynamicRouteService implements ApplicationEventPublisherAware {

    private final RouteDefinitionWriter routeDefinitionWriter;
    private final RouteDefinitionLocator routeDefinitionLocator;
    private final ServiceLoadBalancer multiServiceLoadBalancer;
    private ApplicationEventPublisher publisher;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public DynamicRouteService(RouteDefinitionWriter routeDefinitionWriter,
                               RouteDefinitionLocator routeDefinitionLocator, ServiceLoadBalancer multiServiceLoadBalancer) {
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.routeDefinitionLocator = routeDefinitionLocator;
        this.multiServiceLoadBalancer = multiServiceLoadBalancer;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    @PostConstruct
    public void init() {
        // 初始加载路由
        refreshRoutes();
    }

    @Scheduled(fixedRate = 5000)
    public void refreshRoutes() {
        // 删除所有动态路由
        routeDefinitionLocator.getRouteDefinitions()
                .then(Mono.defer(() -> {
                    return Flux.fromIterable(ClientRouteService.servicesMap.keySet())
                            .flatMap(v -> {
                                RouteDefinition definition = new RouteDefinition();
                                definition.setId(v);
                                try {
                                    definition.setUri(new URI("lb://" + v));
                                } catch (URISyntaxException e) {
                                    throw new RuntimeException(e);
                                }
                                DiscoveryClient.CustomServiceInstance customServiceInstance = (DiscoveryClient.CustomServiceInstance) ClientRouteService.servicesMap.get(v).get(0);
                                definition.setPredicates(Arrays.asList(
                                        new org.springframework.cloud.gateway.handler.predicate.PredicateDefinition(
                                                "Path="+customServiceInstance.getPredicates())));
                                return routeDefinitionWriter.save(Mono.just(definition));
                            }).collectList().then();
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