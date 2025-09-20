package com.commons.gateway;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.HasConfig;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.RouteMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Component
public class MultiServiceLoadBalancerFilter implements GlobalFilter, Ordered {

    private static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = 10100;
    private final MultiServiceLoadBalancer loadBalancer;

    public MultiServiceLoadBalancerFilter(MultiServiceLoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);

        if (url == null || !"lb".equals(url.getScheme())) {
            return chain.filter(exchange);
        }

        String serviceId = url.getHost();
        // 创建请求上下文，包含服务ID
        Request<Object> request = new DefaultRequest<>(route.getId());

        return loadBalancer.choose(request)
            .flatMap(response -> {
                if (!response.hasServer()) {
                    throw new NotFoundException("No instances available for " + serviceId);
                }

                ServiceInstance instance = response.getServer();
                URI newUrl = URI.create(instance.getUri().toString() + url.getPath());

                // 添加查询参数（如果有）
                if (url.getQuery() != null) {
                    newUrl = URI.create(newUrl.toString() + "?" + url.getQuery());
                }

                exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newUrl);
                return chain.filter(exchange);
            });
    }

    // 简单的请求实现
    private static class DefaultRequest<T> implements Request<T> {
        private final T context;

        public DefaultRequest(T context) {
            this.context = context;
        }

        @Override
        public T getContext() {
            return context;
        }
    }
}