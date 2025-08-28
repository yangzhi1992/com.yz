package com.commons.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger-UI 界面：http://localhost:8080/swagger-ui.html
 * OpenAPI 文档的 JSON 文件：http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {
  
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot OpenAPI 示例")
                        .version("1.0.0")
                        .description("这是基于 Spring Boot 的 OpenAPI 文档化示例"));
    }
}
