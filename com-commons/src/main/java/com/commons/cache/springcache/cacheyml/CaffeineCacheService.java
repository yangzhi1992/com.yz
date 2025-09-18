package com.commons.cache.springcache.cacheyml;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CaffeineCacheService {
    @Cacheable("products")
    public String getProductById(String productId) {
        System.out.println("Fetching product from database: " + productId);
        return "Product-" + productId;
    }
}
