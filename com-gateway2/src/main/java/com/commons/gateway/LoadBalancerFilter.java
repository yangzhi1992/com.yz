package com.commons.gateway;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoadBalancerFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoadBalancerFilter.class);
    private final RedisBackendService redisBackendService;
    private final WebClient webClient;

    public LoadBalancerFilter(RedisBackendService redisBackendService, WebClient webClient) {
        this.redisBackendService = redisBackendService;
        this.webClient = webClient;
        logger.info("LoadBalancerFilter initialized");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest()
                              .getURI()
                              .getPath();
        String query = exchange.getRequest()
                               .getURI()
                               .getQuery();

        logger.debug("Processing request: {}?{}", path, query);

        // 检查是否是需要处理的路由
        if (path.startsWith("/")) {
            String xServiceId = exchange.getRequest()
                                        .getHeaders()
                                        .getFirst("x-service-id");
            logger.info("Processing API request: {}?{}", path, query);
            String key = RedisBackendService.BACKEND_SERVICES_LIVECHAT_KEY;
            /*if (path.startsWith("/apis/msg/broker_addr.action") || path.startsWith("/apis/msg/ws_addr.action")) {
                key = RedisBackendService.BACKEND_SERVICES_LIVECHAT_EXTERNAL_KEY;
            } else if(Objects.equals(xServiceId,"prometheus") ||
                    Objects.equals(path,"/") ||
                    Objects.equals(path,"/graph") ||
                    Objects.equals(path,"/favicon.ico") ||
                    Objects.equals(path,"/manifest.json") ||
                    Objects.equals(path,"/static/css/main.132f8bd2.css") ||
                    Objects.equals(path,"/static/js/main.8abd4fa4.js")
            )*/

            key = RedisBackendService.BACKEND_SERVICES_PROMETHEUS_KEY;
            return redisBackendService.getAllServices(key)
                                      .collectList()
                                      .flatMap(services -> {
                                          logger.info("Found {} backend services", services.size());

                                          if (services.isEmpty()) {
                                              logger.warn("No backend services available");
                                              exchange.getResponse()
                                                      .setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                                              return exchange.getResponse()
                                                             .setComplete();
                                          }

                                          // 记录所有服务状态
                                          services.forEach(service ->
                                                  logger.info("Service {}: {}:{} (healthy: {})",
                                                          service.getId(), service.getHost(), service.getPort(),
                                                          service.isHealthy()));

                                          // 过滤出健康实例
                                          List<BackendServiceDTO> healthyServices = services.stream()
                                                                                            .filter(BackendServiceDTO::isHealthy)
                                                                                            .collect(
                                                                                                    Collectors.toList());

                                          logger.info("Found {} healthy services", healthyServices.size());

                                          if (healthyServices.isEmpty()) {
                                              try {
                                                  Thread.sleep(3000);
                                              } catch (InterruptedException e) {
                                                  throw new RuntimeException(e);
                                              }
                                          }

                                          if (healthyServices.isEmpty()) {
                                              logger.warn("No healthy backend services available");
                                              exchange.getResponse()
                                                      .setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                                              return exchange.getResponse()
                                                             .setComplete();
                                          }

                                          // 随机选择一个健康实例
                                          BackendServiceDTO selected = healthyServices.get(
                                                  ThreadLocalRandom.current()
                                                                   .nextInt(healthyServices.size()));

                                          logger.info("Selected backend: {}:{}", selected.getHost(),
                                                  selected.getPort());

                                          // 构建目标URL
                                          String targetUrl = selected.getUrl() + path;
                                          if (query != null) {
                                              targetUrl += "?" + query;
                                          }

                                          logger.info("Forwarding to: {}", targetUrl);

                                          // 使用WebClient执行请求
                                          return executeRequestWithWebClient(exchange, targetUrl);
                                      });
        }

        logger.debug("Skipping non-API request: {}", path);
        // 如果不是API请求，继续过滤器链
        return chain.filter(exchange);
    }

    private Mono<Void> executeRequestWithWebClient(ServerWebExchange exchange, String targetUrl) {
        // 获取请求方法和内容类型
        HttpMethod method = exchange.getRequest()
                                    .getMethod();
        MediaType contentType = exchange.getRequest()
                                        .getHeaders()
                                        .getContentType();

        // 创建WebClient请求构建器
        WebClient.RequestBodySpec requestSpec = webClient
                .method(method)
                .uri(targetUrl)
                .headers(headers -> {
                    headers.addAll(exchange.getRequest()
                                           .getHeaders());
                    headers.add("Authorization", "Basic YWRtaW46eWExNTcxNTY=");
                });

        // 处理请求体
        Mono<Void> result;
        if (method == HttpMethod.GET || method == HttpMethod.HEAD ||
                exchange.getRequest()
                        .getBody() == null) {
            // 对于GET/HEAD请求或没有请求体的请求
            result = requestSpec.exchangeToMono(clientResponse -> {
                exchange.getResponse()
                        .setStatusCode(clientResponse.statusCode());
                exchange.getResponse()
                        .getHeaders()
                        .addAll(clientResponse.headers()
                                              .asHttpHeaders());
                HttpHeaders headers = exchange.getResponse()
                                              .getHeaders();
                if (headers.containsKey(HttpHeaders.TRANSFER_ENCODING)) {
                    headers.remove(HttpHeaders.TRANSFER_ENCODING);
                    headers.set(HttpHeaders.CONTENT_ENCODING, "identity"); // 禁用压缩
                }
                return exchange.getResponse()
                               .writeWith(clientResponse.bodyToFlux(DataBuffer.class));
            });
        } else {
            // 对于有请求体的请求
            result = requestSpec
                    .body(exchange.getRequest()
                                  .getBody(), DataBuffer.class)
                    .exchangeToMono(clientResponse -> {
                        exchange.getResponse()
                                .setStatusCode(clientResponse.statusCode());
                        exchange.getResponse()
                                .getHeaders()
                                .addAll(clientResponse.headers()
                                                      .asHttpHeaders());
                        HttpHeaders headers = exchange.getResponse()
                                                      .getHeaders();
                        if (headers.containsKey(HttpHeaders.TRANSFER_ENCODING)) {
                            headers.remove(HttpHeaders.TRANSFER_ENCODING);
                            headers.set(HttpHeaders.CONTENT_ENCODING, "identity"); // 禁用压缩
                        }
                        return exchange.getResponse()
                                       .writeWith(clientResponse.bodyToFlux(DataBuffer.class));
                    });
        }

        return result.onErrorResume(e -> {
            logger.error("Error forwarding request to {}: {}", targetUrl, e.getMessage());
            exchange.getResponse()
                    .setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse()
                           .setComplete();
        });
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}