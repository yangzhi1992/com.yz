package com.commons.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 请求转发过滤器
 */
@Component
public class RouteForwardingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String originalPath = request.getURI()
                                     .getPath();

        // 根据路径模式进行路由转发
        if (originalPath.startsWith("/com/commons/v1/")) {
            String newPath = originalPath.replace("/com/commons/v1/", "/com/commons/v2/");
            ServerHttpRequest mutatedRequest = request.mutate()
                                                      .path(newPath)
                                                      .build();
            return chain.filter(exchange.mutate()
                                        .request(mutatedRequest)
                                        .build());
        }

        return chain.filter(exchange);
    }
}