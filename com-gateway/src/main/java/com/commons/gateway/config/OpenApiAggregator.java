package com.commons.gateway.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

@Configuration
public class OpenApiAggregator {

    @Autowired
    private RouteLocator routeLocator;
    
    @Bean
    @Primary
    public SwaggerResourcesProvider swaggerResourcesProvider() {
        return () -> {
            List<SwaggerResource> resources = new ArrayList<>();
            
            // 添加网关自身文档
            resources.add(createResource("gateway", "/v3/api-docs"));
            
            // 添加各微服务文档
            routeLocator.getRoutes()
                .filter(route -> route.getUri().getHost() != null)
                .filter(route -> !"openapi".equals(route.getId()))
                .subscribe(route -> {
                    String name = route.getId();
                    String location = "/" + name + "/v3/api-docs";
                    resources.add(createResource(name, location));
                });
            
            return resources;
        };
    }
    
    private SwaggerResource createResource(String name, String location) {
        SwaggerResource resource = new SwaggerResource();
        resource.setName(name);
        resource.setLocation(location);
        resource.setSwaggerVersion("3.0");
        return resource;
    }
}
