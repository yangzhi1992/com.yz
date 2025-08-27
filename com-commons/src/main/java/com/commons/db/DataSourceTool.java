package com.commons.db;

import static com.commons.utils.PropertiesTool.getBoolean;
import static com.commons.utils.PropertiesTool.getInt;
import static com.commons.utils.PropertiesTool.getString;
import static com.commons.utils.PropertiesTool.isLazy;

import com.alibaba.druid.pool.DruidDataSource;
import com.commons.utils.StringTool;
import java.util.List;
import java.util.Properties;
import org.springframework.core.env.Environment;

class DataSourceTool {

    public static void configureDatasource(String key, Properties properties, DruidDataSource ds,
            Environment environment) {
        ds.configFromPropety(properties);

        String driverClassName = getString(properties, "druid.driverClassName");
        if (StringTool.isBlank(driverClassName)) {
            driverClassName = getString(properties, "driverClassName");
        }
        ds.setDriverClassName(driverClassName);
        ds.addConnectionProperty("connectTimeout",
                getString(properties, "druid.connectTimeout", "5000"));//连接超时默认5秒钟
        ds.setUrl(getString(properties, "url"));
        ds.setUsername(getString(properties, "username"));
        ds.setPassword(getString(properties, "password"));

        ds.setInitialSize(getInt(properties, "druid.initialSize", 1));//配置初始化大小
        ds.setMinIdle(getInt(properties, "druid.minIdle", 5));//配置最小连接数
        ds.setMaxActive(getInt(properties, "druid.maxActive", 5));//配置最大连接数
        ds.setMaxWait(getInt(properties, "druid.maxWait", 500));//配置获取连接的最大等待时间,默认为500豪秒
        ds.setTimeBetweenEvictionRunsMillis(
                getInt(properties, "druid.timeBetweenEvictionRunsMillis",
                        60000));//配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        ds.setMinEvictableIdleTimeMillis(
                getInt(properties, "druid.minEvictableIdleTimeMillis",
                        300000));//配置一个连接在池中最小生存的时间，单位是毫秒
        ds.setTestOnReturn(getBoolean(properties, "druid.testOnReturn", false));
        //打开PSCache，并且指定每个连接上PSCache的大小
        ds.setPoolPreparedStatements(getBoolean(properties, "druid.poolPreparedStatements", false));
        ds.setMaxPoolPreparedStatementPerConnectionSize(
                getInt(properties, "druid.maxPoolPreparedStatementPerConnectionSize", 20));
        int queryTimeout = getInt(properties, "druid.queryTimeout", 0);
        if (queryTimeout > 0) {
            ds.setQueryTimeout(queryTimeout);
        }
        int transactionQueryTimeout = getInt(properties, "druid.transactionQueryTimeout", 0);
        if (transactionQueryTimeout > 0) {
            ds.setTransactionQueryTimeout(transactionQueryTimeout);
        }
        int validationQueryTimeout = getInt(properties, "druid.validationQueryTimeout", 0);
        if (validationQueryTimeout > 0) {
            ds.setValidationQueryTimeout(validationQueryTimeout);
        }

        String initSqls = getString(properties, "druid.connectionInitSqls", null);
        if (StringTool.isNotBlank(initSqls)) {
            List<String> connectionInitSqls = StringTool.split(initSqls, ',', 1);
            ds.setConnectionInitSqls(connectionInitSqls);
        }
        boolean lazyInit = isLazy(environment, key, "db");
        if (lazyInit) {//如果延迟加载,不预先创建连接
            ds.setInitialSize(0);
        }
    }


}
