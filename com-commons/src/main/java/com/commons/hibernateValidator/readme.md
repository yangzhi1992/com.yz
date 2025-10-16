1、引入jar
<!-- Hibernate Validator -->
<!-- Hibernate Validator 主依赖 -->
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.2.5.Final</version>  <!-- 最后一个完全支持Java 8的稳定版本 -->
</dependency>
<!-- Bean Validation API (JSR 380) -->
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
    <version>2.0.1.Final</version>
</dependency>
<!-- 表达式语言依赖 (必需) -->
<dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>javax.el</artifactId>
    <version>3.0.1-b12</version>  <!-- 或 3.0.0 -->
</dependency>

2、集成spring
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
    <version>${spring.boot.version}</version>
</dependency>

2.1 分组验证
    2.1.1 第一验证组 CreateGroup,UpdateGroup
    2.1.2 在DTO中使用分组 UserDto
    2.1.3 Controller中使用分组 HibernateValidatorController
2.2 自定义注解
    2.1.1 创建自定义注解 PhoneNumber
    2.1.2 实现验证逻辑 PhoneNumberValidator
    2.1.3 在DTO中使用 UserDto
2.3 Service 层验证
    2.3.1 方法验证配置
    2.3.2 Service 方法验证
2.4 全局异常处理 GlobalExceptionHandler
2.5 controller层验证 HibernateValidatorController
2.6 自定义ValidatorFactory ValidatorConfig 
2.7 国际化错误消息 UserDto,validation-messages.properties