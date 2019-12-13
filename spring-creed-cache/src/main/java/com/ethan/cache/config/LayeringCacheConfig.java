package com.ethan.cache.config;

import com.ethan.cache.LayeringCacheManager;
import com.ethan.context.utils.InstanceUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class LayeringCacheConfig {
  private final CacheProperties cacheProperties;

  public LayeringCacheConfig(CacheProperties cacheProperties) {
    this.cacheProperties = cacheProperties;
  }

  @Bean
  @Primary
  public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
    StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer(InstanceUtils.getMapperInstance()));
    template.afterPropertiesSet();
    return template;
  }


  @Bean("cacheManager")
  @Primary
  public CacheManager cacheManager(RedisTemplate<String, String> redisTemplate, RedisConnectionFactory connectionFactory) {
    LayeringCacheManager layeringCacheManager = new LayeringCacheManager(redisTemplate, connectionFactory, cacheProperties);
    // Allow null to prevent cache breakdown
    layeringCacheManager.setAllowNullValues(true);
    return layeringCacheManager;
  }

  /**
   * Show declaration cache key generator
   * @return KeyGenerator
   */
  @Bean
  public KeyGenerator keyGenerator() {
    return new SimpleKeyGenerator();
  }
}
