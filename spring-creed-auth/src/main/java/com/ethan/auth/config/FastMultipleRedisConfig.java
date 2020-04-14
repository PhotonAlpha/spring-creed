package com.ethan.auth.config;

import com.ethan.context.utils.InstanceUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;

import java.util.Optional;

/**
 * redis 多环境配置, 应用于不同的 redisTemplate
 */
@Configuration
public class FastMultipleRedisConfig {
  @Bean
  @ConfigurationProperties("multi")
  public FastMultipleRedisProperties fastMultipleRedisProperties() {
    return new FastMultipleRedisProperties();
  }

  @Bean
  @SpringSessionRedisConnectionFactory
  public RedisConnectionFactory redisSessionConnectionFactory(FastMultipleRedisProperties redisProperties) {
    RedisProperties redisProp = Optional.ofNullable(redisProperties)
        .map(FastMultipleRedisProperties :: getRedis)
        .map(map -> map.get("redisSession"))
        .orElse(new RedisProperties());
    return getLettuceConnectionFactory(redisProp, "redisSession");
  }
  @Bean
  public RedisConnectionFactory redisOAuthConnectionFactory(FastMultipleRedisProperties redisProperties) {
    RedisProperties redisProp = Optional.ofNullable(redisProperties)
        .map(FastMultipleRedisProperties :: getRedis)
        .map(map -> map.get("redisOAuth"))
        .orElse(new RedisProperties());
    return getLettuceConnectionFactory(redisProp, "redisOAuth");
  }
  @Bean
  public RedisConnectionFactory redisCacheConnectionFactory(FastMultipleRedisProperties redisProperties) {
    RedisProperties redisProp = Optional.ofNullable(redisProperties)
        .map(FastMultipleRedisProperties :: getRedis)
        .map(map -> map.get("redisCache"))
        .orElse(new RedisProperties());
    return getLettuceConnectionFactory(redisProp, "redisCache");
  }
  @Primary
  @Bean
  public RedisConnectionFactory redisMainConnectionFactory(FastMultipleRedisProperties redisProperties) {
    RedisProperties redisProp = Optional.ofNullable(redisProperties)
        .map(FastMultipleRedisProperties :: getRedis)
        .map(map -> map.get("redisMain"))
        .orElse(new RedisProperties());
    return getLettuceConnectionFactory(redisProp, "redisCache");
  }

  @Bean
  public RedisTemplate<String, String> redisStringTemplate(@Qualifier("redisSessionConnectionFactory") RedisConnectionFactory redisSessionConnectionFactory) {
    return new StringRedisTemplate(redisSessionConnectionFactory);
  }
  @Bean
  public RedisTemplate<String, String> redisJacksonTemplate(@Qualifier("redisOAuthConnectionFactory") RedisConnectionFactory redisSessionConnectionFactory) {
    RedisTemplate template = new RedisTemplate<String, Object>();
    template.setConnectionFactory(redisSessionConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer(InstanceUtils.getMapperInstance()));
    template.afterPropertiesSet();
    return template;
  }

  private LettuceConnectionFactory getLettuceConnectionFactory(RedisProperties redisProp, String clientName) {
    RedisProperties.Lettuce lettuceProp = redisProp.getLettuce();

    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
    configuration.setHostName(redisProp.getHost());
    configuration.setPort(redisProp.getPort());
    configuration.setDatabase(redisProp.getDatabase());
    if (StringUtils.isNotEmpty(redisProp.getPassword())) {
      configuration.setPassword(RedisPassword.of(redisProp.getPassword()));
    }

    // GenericObjectPoolConfig
    GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
    genericObjectPoolConfig.setMaxIdle(lettuceProp.getPool().getMaxIdle());
    genericObjectPoolConfig.setMaxTotal(lettuceProp.getPool().getMaxActive());
    genericObjectPoolConfig.setMinIdle(lettuceProp.getPool().getMinIdle());
    genericObjectPoolConfig.setMaxWaitMillis(lettuceProp.getPool().getMaxWait().toMillis());

    LettucePoolingClientConfiguration.builder()
        .shutdownTimeout(lettuceProp.getShutdownTimeout())
        .poolConfig(genericObjectPoolConfig).build();


    LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
        .clientName(clientName)
        .shutdownTimeout(redisProp.getLettuce().getShutdownTimeout())
        .build();

    return new LettuceConnectionFactory(configuration, clientConfiguration);
  }
}
