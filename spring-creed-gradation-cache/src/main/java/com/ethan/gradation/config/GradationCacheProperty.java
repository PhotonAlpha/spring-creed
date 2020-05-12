package com.ethan.gradation.config;

import lombok.Data;

import java.io.Serializable;

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
    private CaffeineCacheProperty caffeineCacheProperty;

    /**
     * 二级缓存配置
     */
    private RedisCacheProperty redisCacheProperty;

    public GradationCacheProperty(CaffeineCacheProperty caffeineCacheProperty, RedisCacheProperty redisCacheProperty, String depict) {
        this.caffeineCacheProperty = caffeineCacheProperty;
        this.redisCacheProperty = redisCacheProperty;
        this.depict = depict;
    }

    public GradationCacheProperty(CaffeineCacheProperty caffeineCacheProperty, RedisCacheProperty redisCacheProperty) {
        this.caffeineCacheProperty = caffeineCacheProperty;
        this.redisCacheProperty = redisCacheProperty;
    }

    public GradationCacheProperty() {
    }
}
