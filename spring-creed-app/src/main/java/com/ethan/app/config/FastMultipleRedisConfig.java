package com.ethan.app.config;

import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.function.Supplier;

@Configuration
public class FastMultipleRedisConfig {
  @Bean
  public RedisTemplate<String, String> redisStringTemplate() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("server", 6379);
    LettuceConnectionFactory lettuceFactory = new LettuceConnectionFactory(config);

    //RedisConnectionFactory connectionFactory =
    return new StringRedisTemplate();
  }
}
