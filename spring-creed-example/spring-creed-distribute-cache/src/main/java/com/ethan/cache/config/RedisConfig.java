package com.ethan.cache.config;

import com.ethan.common.utils.json.JacksonUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

//@Configuration
@Deprecated
public class RedisConfig {

  @Bean
  public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    return new RedisCacheManager(
        RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory),
        getRedisCacheConfigurationWithTtl(5),
        getRedisCacheConfigurationMap()
    );
  }

  private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(long seconds) {
    RedisSerializationContext.SerializationPair<String> keySerializer = RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());
    RedisSerializationContext.SerializationPair<Object> valueSerializer =
        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(JacksonUtils.objectMapper()));

    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofSeconds(seconds))
        .serializeKeysWith(keySerializer)
        .serializeValuesWith(valueSerializer);
  }

  private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
    Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
    redisCacheConfigurationMap.put("SsoCache", this.getRedisCacheConfigurationWithTtl(24*60*60));
    redisCacheConfigurationMap.put("BasicDataCache", this.getRedisCacheConfigurationWithTtl(30));
    return redisCacheConfigurationMap;
  }

  @Bean("redisJackson")
  public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
    StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer(JacksonUtils.objectMapper()));
    template.afterPropertiesSet();
    return template;
  }

  /**
   * define the cache key generator
   * @return SimpleKeyGenerator
   */
  @Bean
  public KeyGenerator keyGenerator() {
    return new SimpleKeyGenerator();
  }
}
