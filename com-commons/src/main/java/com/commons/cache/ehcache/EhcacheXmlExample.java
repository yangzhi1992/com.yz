package com.commons.cache.ehcache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.xml.XmlConfiguration;

import java.net.URL;

public class EhcacheXmlExample {
    public static void main(String[] args) throws Exception {
        // 1. 加载 XML 配置文件
        URL configFile = EhcacheXmlExample.class.getResource("/ehcache.xml");
        CacheManager cacheManager = org.ehcache.config.builders.CacheManagerBuilder.newCacheManager(new XmlConfiguration(configFile));
        cacheManager.init();

        // 2. 获取 XML 定义的缓存
        Cache<String, String> userCache = cacheManager.getCache("userCache", String.class, String.class);

        // 3. 使用缓存 (增/查)
        userCache.put("userId:1", "John");
        System.out.println(userCache.get("userId:1")); // 输出 John

        // 4. 关闭 CacheManager
        cacheManager.close();
    }
}
