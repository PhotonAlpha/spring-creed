package com.ethan.app.config;

import com.ethan.gradation.config.CaffeineCacheProperty;
import com.ethan.gradation.config.GradationCacheProperty;
import com.ethan.gradation.config.RedisCacheProperty;
import com.ethan.gradation.listener.CaffeineRemovalListener;
import com.ethan.gradation.manager.GradationCacheManager;
import com.ethan.redis.multiple.FastRedisRegister;
import com.google.common.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@FastRedisRegister(exclude = "redisSession")
public class CacheConfiguration {
  @Bean
  public CaffeineRemovalListener caffeineRemovalListener() {
    return new CaffeineRemovalListener();
  }

  @Bean
  public CacheManager cacheManager(RedisTemplate redisCacheRedisTemplate) {
    GradationCacheProperty prop = new GradationCacheProperty();
    prop.setCaffeineCacheProperty(new CaffeineCacheProperty());
    prop.setRedisCacheProperty(new RedisCacheProperty());

    return new GradationCacheManager(prop, redisCacheRedisTemplate, caffeineRemovalListener());
  }
  /*@Bean
  public CacheKeyPrefix cacheKeyPrefix() {
    return CacheKeyPrefix.simple();
  }

  @Bean
  public CacheManager cacheManager(RedisTemplate redisCacheRedisTemplate) {

    RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
        //设置key为String
        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
        // 设置value 为自动转Json的Object
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer()))
        // 不缓存null
        .disableCachingNullValues()
        //缓存数据时长
        .entryTtl(Duration.ofSeconds(100))
        // 前缀
        .computePrefixWith(cacheKeyPrefix());
    RedisCacheConfiguration defaultCacheConfiguration2 = RedisCacheConfiguration.defaultCacheConfig()
        //设置key为String
        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
        // 设置value 为自动转Json的Object
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer()))
        // 不缓存null
        .disableCachingNullValues()
        //缓存数据时长
        .entryTtl(Duration.ofSeconds(200))
        // 前缀
        .computePrefixWith(cacheKeyPrefix());

    Map<String, RedisCacheConfiguration> config = new HashMap<>();
    config.put("cache1", defaultCacheConfiguration);
    config.put("cache2", defaultCacheConfiguration2);

    RedisCacheManager caches = RedisCacheManager.RedisCacheManagerBuilder
        // redis 连接工厂
        .fromConnectionFactory(redisCacheRedisTemplate.getConnectionFactory())
        // 缓存配置
        .cacheDefaults(defaultCacheConfiguration)

        .withInitialCacheConfigurations(config)

        // 配置同步修改或删除 put/evict
        .transactionAware()
        .build();

    caches.getCache("cache1");

    //SimpleCacheManager manager = new SimpleCacheManager();
    //manager.setCaches(Arrays.asList(caches));
    return caches;
  }*/
}
