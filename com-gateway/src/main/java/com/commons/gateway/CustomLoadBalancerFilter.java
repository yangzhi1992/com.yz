package com.commons.gateway;

import java.net.URI;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomLoadBalancerFilter implements GlobalFilter, Ordered {

    private final LoadBalancer loadBalancer = new LoadBalancer();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Route routeId = exchange.getAttribute("org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayRoute");
        String originalPath = request.getURI().getPath();
        if (routeId == null) {
            return chain.filter(exchange); // 未匹配则直接执行下游过滤器
        }

        // 根据负载均衡选择目标 URI
        URI targetUri;
        try {
            targetUri = loadBalancer.choose(routeId.getId());
        } catch (IllegalStateException e) {
            // 如果服务不可用，则返回 503 错误
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE);
            return exchange.getResponse().setComplete();
        }

        // 重写请求路径和 Host
        URI newUri = URI.create(targetUri.toString() + originalPath);
        ServerHttpRequest newRequest = request.mutate()
                .uri(newUri)
                .header("Host", targetUri.getHost())
                .build();

        // 将修改后的请求对象设置到 Exchange 中
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    /**
     * 从路径中提取服务 ID
     * 例如："/routec/some-path" -> "routec"
     */
    private String extractServiceId(String originalPath) {
        String[] segments = originalPath.split("/");
        return (segments.length > 1) ? segments[1] : null;
    }

    @Override
    public int getOrder() {
        return -1; // 高优先级
    }
}
