package com.ethan.system.config;

import com.ethan.system.dal.registration.JpaRegisteredClientRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import java.time.Duration;
import java.util.List;

/**
 * Cache 配置类，基于 Redis 实现
 */
@Configuration
public class OAuth2CacheManagerConfiguration {
    public static final String OAUTH2_REDIS_TEMPLATE = "OAUTH2_REDIS_TEMPLATE";
    public static final String OAUTH2_CACHE_MANAGER = "OAUTH2_CACHE_MANAGER";

    /**
     * 创建 RedisTemplate Bean，使用 JSON 序列化方式
     */
    @Bean(name = OAUTH2_REDIS_TEMPLATE)
    public RedisTemplate<String, Object> oauth2RedisTemplate(RedisConnectionFactory factory) {
        // 创建 RedisTemplate 对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置 RedisConnection 工厂。 它就是实现多种 Java Redis 客户端接入的秘密工厂。感兴趣的胖友，可以自己去撸下。
        template.setConnectionFactory(factory);
        // 使用 String 序列化方式，序列化 KEY 。
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        // 使用 JSON 序列化方式（库是 Jackson ），序列化 VALUE 。

        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
        valueSerializer.configure(mapper -> {
            // 解决Java8 Instant 时间类无法序列化问题
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            //添加oauth2 序列化模块
            ClassLoader classLoader = JpaRegisteredClientRepository.class.getClassLoader();
            List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
            mapper.registerModules(securityModules);
            mapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        });

        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        return template;
    }

    @Bean(name = OAUTH2_CACHE_MANAGER)
    public CacheManager oauth2CacheManager(@Qualifier(OAUTH2_REDIS_TEMPLATE) RedisTemplate<String, Object> template) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                // 设置key为string
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getStringSerializer()))
                // 设置value自动转Object为String
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getValueSerializer()))
                // 不缓存null
                .disableCachingNullValues()
                .computePrefixWith(CacheKeyPrefix.prefixed("oauth2::"))
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
