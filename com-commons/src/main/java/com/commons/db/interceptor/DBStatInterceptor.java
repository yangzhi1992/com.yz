package com.commons.db.interceptor;

import com.commons.db.DynamicDataSource;
import com.commons.db.annotation.DBKey;
import com.commons.utils.StringTool;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Value;

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class,
        Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class,
                Object.class,
                RowBounds.class, ResultHandler.class})})
public class DBStatInterceptor
        implements Interceptor {

    @Value("${db.stat.logSlowSql:true}")
    private boolean logSlowSql;

    @Value("${db.stat.slowSqlMillis:200}")
    private long slowSqlMillis;

    @Value("${db.stat.seriousSlowSqlMillis:2000}")
    private long seriousSlowSqlMillis;

    @Value("${db.stat.maxParameters:100}")
    private int maxParameters;

    @Value("${log.databaseRand:100}")
    private int tickRand;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long start = System.nanoTime();
        String dbId = DynamicDataSource.getDataSourceKey();
        Throwable error = null;
        try {
            return invocation.proceed();
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            DynamicDataSource.clearDataSourceKey();
            long costs = (System.nanoTime() - start) / 1000_000;
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            String sqlId = mappedStatement.getId();

            dbId = StringTool.isBlank(dbId) ? DBKey.DEFAULT : dbId;

            Object parameter = null;
            if (invocation.getArgs().length > 1) {
                parameter = invocation.getArgs()[1];
            }
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            String sql = null;
            Throwable recordErr = null;
            try {//忽略异常
                Configuration configuration = mappedStatement.getConfiguration();
                sql = buildSql(configuration, boundSql);
            } catch (Exception e) {
                recordErr = e;
            }
            if (error != null || costs > slowSqlMillis) {
                if (error != null) {
                } else {
                    if (costs > seriousSlowSqlMillis) {
                        //慢sql统计
                    } else {
                       //慢sql统计
                    }
                }
            }

            String escaped = StringUtils
                    .replace(StringUtils.replace(boundSql.getSql(), "\r\n", " "), "\n", " ");
        }
    }

    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            String str = (String) obj;
            str = str.replaceAll("\\$", "\\\\\\$");
            value = "'" + str + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat
                    .getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format((Date) obj) + "'";
        } else {
            value = (obj != null) ? obj.toString() : "";
        }
        return value;
    }

    public String buildSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {

                sql = StringUtils.replaceOnce(sql, "?", getParameterValue(parameterObject));

            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                int i = 0;
                for (ParameterMapping parameterMapping : parameterMappings) {
                    if (++i > maxParameters) {
                        break;
                    }
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = StringUtils.replaceOnce(sql, "?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = StringUtils.replaceOnce(sql, "?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // no need
    }
}
