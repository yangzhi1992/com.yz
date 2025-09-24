package com.commons.gateway.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 响应缓存过滤器
 */
@Component
public class ResponseCacheFilter implements GlobalFilter {

    private final Cache<String, CachedResponse> cache = Caffeine.newBuilder()
                                                                .expireAfterWrite(5, TimeUnit.MINUTES)
                                                                .maximumSize(1000)
                                                                .build();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String cacheKey = generateCacheKey(exchange.getRequest());
        
        CachedResponse cached = cache.getIfPresent(cacheKey);
        if (cached != null) {
            return writeCachedResponse(exchange.getResponse(), cached);
        }
        
        // 缓存响应
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(exchange.getResponse()) {
            private final List<DataBuffer> bodyBuffers = new ArrayList<>();

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                return Flux.from(body)
                           .doOnNext(bodyBuffers::add)
                           .then(Mono.defer(() -> {
                        // 缓存响应体
                        byte[] combinedBytes = bodyBuffers.stream()
                                .map(DataBuffer::asByteBuffer)
                                .reduce(ByteBuffer.allocate(0), (a, b) -> {
                                    ByteBuffer combined = ByteBuffer.allocate(a.remaining() + b.remaining());
                                    combined.put(a);
                                    combined.put(b);
                                    combined.flip();
                                    return combined;
                                })
                                .array();
                        
                        CachedResponse cachedResponse = new CachedResponse(
                            exchange.getResponse().getStatusCode(),
                            exchange.getResponse().getHeaders(),
                            combinedBytes
                        );
                        cache.put(cacheKey, cachedResponse);
                        
                        return super.writeWith(Flux.fromIterable(bodyBuffers));
                    }));
            }
        };
        
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }
    
    private String generateCacheKey(ServerHttpRequest request) {
        return request.getMethod() + ":" + request.getURI().getPath();
    }
    
    private Mono<Void> writeCachedResponse(ServerHttpResponse response, CachedResponse cached) {
        response.setStatusCode(cached.getStatusCode());
        response.getHeaders().putAll(cached.getHeaders());
        DataBuffer buffer = response.bufferFactory().wrap(cached.getBody());
        return response.writeWith(Mono.just(buffer));
    }
    
    @Data
    @AllArgsConstructor
    private static class CachedResponse {
        private HttpStatus statusCode;
        private HttpHeaders headers;
        private byte[] body;
    }
}