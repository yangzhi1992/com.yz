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