package com.vanlooy.similarproducts.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String SIMILAR_IDS = "similar-ids";
    public static final String PRODUCT_DETAILS = "product-details";
}
