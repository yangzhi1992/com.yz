package com.commons.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.commons.db.interceptor.DBStatInterceptor;
import com.commons.common.exception.ConfigException;
import com.commons.common.utils.ArrayTool;
import com.commons.common.utils.CollectionTool;
import com.commons.common.utils.PropertiesTool;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@Configuration
@ConditionalOnProperty(prefix = "components.db", name = "enabled", matchIfMissing = false)
@MapperScan(basePackages = "com.commons.db.mapper", sqlSessionTemplateRef = "sqlSessionTemplate")
public class DataSourceConfig
        implements ApplicationContextAware, EnvironmentAware, ApplicationListener<ApplicationEvent> {

    private ApplicationContext applicationContext;

    private Environment environment;

    @Bean
    public MybatisProperties mybatisProperties() {
        MybatisProperties mybatisProperties = new MybatisProperties();
        PropertiesTool.bind(mybatisProperties, environment);
        if (CollectionTool.isBlank(mybatisProperties.getNodes())) {
            throw new ConfigException("database配置文件错误,请检查mybatis.nodes是否为空!");
        }
        return mybatisProperties;
    }

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    @Autowired
    public DataSource dataSource(MybatisProperties mybatisProperties) {
        Map<Object, Object> dsMap = new HashMap<>(mybatisProperties.getNodes()
                                                                   .size());
        for (String nodeName : mybatisProperties.getNodes()
                                                .keySet()) {
            dsMap.put(nodeName, buildDataSource(nodeName, mybatisProperties));
            DynamicDataSource.setDataSourceKey(nodeName);
        }
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(dsMap);
        if (null == dsMap.get("default")) {
            throw new RuntimeException(
                    String.format("Default DataSource [%s] not exists", "default"));
        }
        dynamicDataSource.setDefaultTargetDataSource(dsMap.get("default"));
        return dynamicDataSource;
    }

    @Bean
    @ConditionalOnMissingBean(SqlSessionFactory.class)
    @Autowired
    public SqlSessionFactory sqlSessionFactory(
            DataSource dataSource,
            MybatisProperties mybatisProperties) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();

        try {
            bean.setDataSource(dataSource);
            bean.setConfigLocation(resolver.getResource(mybatisProperties.getConfigLocation()));

            List<Resource> resourcesList = new ArrayList<>();
            for (String mapperLocation : mybatisProperties.getMapperLocations()
                                                          .split(",")) {
                Resource[] resources = resolver.getResources(mapperLocation);
                ArrayTool.add(resourcesList, resources);
            }
            bean.setMapperLocations(resourcesList.toArray(new Resource[resourcesList.size()]));
            bean.setPlugins(createInterceptors());
            return bean.getObject();
        } catch (Exception e) {
            throw new ConfigException("创建Mybatis SqlSessionFactory出错", e);
        }
    }

    @Bean
    @Autowired
    public MapperScannerConfigurer mapperScannerConfigurer(MybatisProperties mybatisProperties) {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setApplicationContext(applicationContext);
        configurer.setAddToConfig(true);
        configurer.setAnnotationClass(Mapper.class);
        configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        configurer.setBasePackage(mybatisProperties.getBasePackage());
        return configurer;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    private Interceptor[] createInterceptors() {
        Interceptor[] interceptors = new Interceptor[1];
        DBStatInterceptor dbTickInterceptor =
                applicationContext.getAutowireCapableBeanFactory()
                                  .createBean(DBStatInterceptor.class);

        interceptors[0] = dbTickInterceptor;
        return interceptors;
    }

    /**
     * 创建单个 DataSource
     */
    private DataSource buildDataSource(String key, MybatisProperties mybatisProperties) {
        Map<String, Object> conf = (Map<String, Object>)mybatisProperties.getNodes()
                                                                         .get(key);
        DruidDataSource ds = new DruidDataSource();
        Properties properties = new Properties();
        properties.putAll(mybatisProperties.getParams());
        properties.putAll(conf);
        DataSourceTool.configureDatasource(key, properties, ds, environment);
        ds.setName(key);

        try {
            ds.init();
        } catch (SQLException e) {
            throw new ConfigException(String.format("创建数据源[%s]失败!", key), e);
        }
        return ds;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}