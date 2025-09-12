1、应用jar
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
        <version>${spring.boot.version}</version>
    </dependency>
2、配置文件
    security.yml
3、注解进行权限控制
    SecurityController

Spring Security 常见核心功能
功能描述
    认证->验证用户身份（如用户名和密码）  基于角色的授权,使用注解进行权限控制
    授权->验证用户是否有权限访问某些资源
    CSRF保护->防止跨站点请求伪造（Cross-Site Request Forgery-）  
        Spring Security 默认会启用 CSRF（跨站点请求伪造）保护。如果需要关闭 CSRF，可以如下操作（如实现 REST 风格 API 时）：
        http.csrf().disable();
    会话管理->提供 Session 管理、并发会话控制
        http.sessionManagement()
        .maximumSessions(1)  // 同一账号只允许一个会话
        .maxSessionsPreventsLogin(true); // 超出限制时防止新登录
    表单登录->提供基于表单的登录支持，以及页面重定向
        http.formLogin().loginPage("/my-login")
    HTTPBasic和Bearer->支持HTTP Basic 和 Bearer Token（常用于 API 认证）
    加密->提供内置的密码加密工具和最佳实践
    自定义扩展->灵活的接口支持自定义认证流程、授权规则、过滤器链等
    集成->与 OAuth2、JWT、LDAP 等轻松集成

