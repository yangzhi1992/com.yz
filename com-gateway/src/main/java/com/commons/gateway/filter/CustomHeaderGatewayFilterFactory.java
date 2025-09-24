package com.commons.gateway.filter;

import com.commons.gateway.filter.CustomHeaderGatewayFilterFactory.Config;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

/**
 * 自定义请求头过滤器
 */
@Component
public class CustomHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {

    public CustomHeaderGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest().mutate()
                                                .header(config.getHeaderName(), config.getHeaderValue())
                                                .build();
            
            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    public static class Config {
        private String headerName;
        private String headerValue;

        // getters and setters
        public String getHeaderName() { return headerName; }
        public void setHeaderName(String headerName) { this.headerName = headerName; }
        public String getHeaderValue() { return headerValue; }
        public void setHeaderValue(String headerValue) { this.headerValue = headerValue; }
    }
}