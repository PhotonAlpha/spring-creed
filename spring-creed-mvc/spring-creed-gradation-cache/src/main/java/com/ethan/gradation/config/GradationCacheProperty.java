package com.ethan.gradation.config;

import lombok.Data;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * 多级缓存配置项
 */
@Data
public class GradationCacheProperty implements Serializable {
    private static final String SPLIT = "-";
    /**
     * 描述，数据监控页面使用
     */
    private String depict;

    /**
     * 是否使用一级缓存
     */
    boolean useFirstCache = true;

    boolean allowNullValue = false;

    /**
     * 一级缓存配置
     */
    private CaffeineCacheProperty caffeineCache;

    /**
     * 二级缓存配置
     */
    private RedisCacheProperty redisCache;

    public GradationCacheProperty(CaffeineCacheProperty caffeineCacheProperty, RedisCacheProperty redisCacheProperty, String depict) {
        this.caffeineCache = caffeineCacheProperty;
        this.redisCache = redisCacheProperty;
        this.depict = depict;
    }

    public GradationCacheProperty(CaffeineCacheProperty caffeineCacheProperty, RedisCacheProperty redisCacheProperty) {
        this.caffeineCache = caffeineCacheProperty;
        this.redisCache = redisCacheProperty;
    }
    public GradationCacheProperty(Supplier<CaffeineCacheProperty> caffeineSupplier, Supplier<RedisCacheProperty> redisSupplier) {
        this.caffeineCache = caffeineSupplier.get();
        this.redisCache = redisSupplier.get();
    }

    public GradationCacheProperty() {
    }
}
