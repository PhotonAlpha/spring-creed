package com.ethan.app.config;

import com.ethan.gradation.config.GradationConfigurationMapper;
import com.ethan.gradation.listener.CaffeineRemovalListener;
import com.ethan.gradation.manager.GradationCacheManager;
import com.ethan.redis.multiple.FastRedisRegister;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@FastRedisRegister(exclude = "redisSession")
public class CacheConfiguration {
  @Bean
  public CaffeineRemovalListener caffeineRemovalListener() {
    return new CaffeineRemovalListener();
  }

  @Bean
  public GradationConfigurationMapper gradationConfigurationList() {
    return new GradationConfigurationMapper();
  }

  @Bean
  public CacheManager cacheManager(RedisTemplate redisCacheRedisTemplate, GradationConfigurationMapper gradationConfigurationList) {
    return GradationCacheManager.GradationCacheManagerBuilder.fromRedisTemplate(redisCacheRedisTemplate)
        .withCaffeineRemovalListener(caffeineRemovalListener())
        .withInitialCacheConfigurations(gradationConfigurationList.getGradation())
        .build();
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
