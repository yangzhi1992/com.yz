package com.commons.db;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
    
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
    
    public static void setDataSourceKey(String dataSourceKey) {
        CONTEXT_HOLDER.set(dataSourceKey);
    }
    
    public static String getDataSourceKey() {
        return CONTEXT_HOLDER.get();
    }
    
    public static void clearDataSourceKey() {
        CONTEXT_HOLDER.remove();
    }
    
    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSourceKey();
    }
}
