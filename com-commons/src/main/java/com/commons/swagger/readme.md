1、引用jar
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-ui</artifactId>
        <version>1.7.0</version> <!-- 使用最新版本 -->
    </dependency>
2、功能
    支持自动扫描 @RestController 和 @RequestMapping。
    默认生成 OpenAPI 3 规范的文档。
    自动加载 Swagger UI 页面（路径 /swagger-ui.html 或 /swagger-ui）。
3、请求url
    Swagger-UI 界面：http://localhost:8080/swagger-ui.html
    OpenAPI 文档的 JSON 文件：http://localhost:8080/v3/api-docs