package com.commons.db;

import java.io.Serializable;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 */
@ConfigurationProperties(prefix = "mybatis")
public class MybatisProperties implements Serializable {

    private static final long serialVersionUID = 1428562649168241447L;
    /**
     * 自动注入配置
     */

    private Map<String, String> params;

    private Map<String, Object> nodes;

    /**
     * mapper文件路径:多个location以,分隔
     */
    private String mapperLocations = "classpath*:mapper/*.xml";

    /**
     * Mapper类所在的base package
     */
    private String basePackage = "com.commons.db.mapper";

    /**
     * mybatis配置文件路径
     */
    private String configLocation = "classpath:mybatis-config.xml";

    /**
     * mapper aop拦截路径，一般不需要修改
     */
    private String  expression= "execution(* com.iqiyi..*.repository.*.*(..))";


    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, Object> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, Object> nodes) {
        this.nodes = nodes;
    }

    public String getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
