1、引用jar
    <!-- jsckson -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-annotations</artifactId>
        <version>2.15.2</version>
    </dependency>
    <!-- 如果使用完整的 Jackson 功能 -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.2</version>
    </dependency>
    <!-- 如果需要处理 Java 8 时间 API -->
    <dependency>
        <groupId>com.fasterxml.jackson.datatype</groupId>
        <artifactId>jackson-datatype-jsr310</artifactId>
        <version>2.15.2</version>
    </dependency>
    <!-- jsckson -->
2、基本使用
    2.1 传统 Date 类型 DateEntity
    2.2 Java 8 时间 API JavaTimeEntity
    2.3 数字格式化 NumberEntity
    2.4 枚举类型格式化 EnumEntity
    2.5 完整示例和测试 JsonFormatExample
3、Spring Boot 中的使用->配置全局日期格式 JacksonConfig
4、基础用法 BaseJackson
    
    
