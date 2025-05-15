package com.ethan.redis.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Cache 配置类，基于 Redis 实现
 */
@Configuration
@EnableCaching
public class CreedCacheAutoConfiguration {

    /**
     * RedisCacheConfiguration Bean
     * <p>
     * 参考 org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration 的 createConfiguration 方法
     */
    /* @Bean
    @Primary
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        // 设置使用 JSON 序列化方式
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));

        // 设置 CacheProperties.Redis 的属性
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    } */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisTemplate<String, Object> template) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                // 设置key为string
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getStringSerializer()))
                // 设置value自动转Object为String
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getValueSerializer()))
                // 不缓存null
                .disableCachingNullValues()
                .computePrefixWith(CacheKeyPrefix.prefixed("creed::"))
                .entryTtl(Duration.ofHours(1));
        return RedisCacheManager.RedisCacheManagerBuilder
                // redis连接工厂
                .fromConnectionFactory(template.getConnectionFactory())

                // 缓存配置
                .cacheDefaults(configuration)
                // 配置同步修改或者删除 put/evict
                .transactionAware()
                .build();
    }
}
