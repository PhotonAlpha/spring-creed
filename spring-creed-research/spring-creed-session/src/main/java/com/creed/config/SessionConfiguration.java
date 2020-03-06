package com.creed.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class SessionConfiguration {
  /**
   * 创建 {@link org.springframework.session.data.redis.RedisOperationsSessionRepository} 使用的 RedisSerializer Bean 。
   *
   * 具体可以看看 {@link org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration#setDefaultRedisSerializer(RedisSerializer)} 方法，
   * 它会引入名字为 "springSessionDefaultRedisSerializer" 的 Bean 。
   *
   * @return RedisSerializer Bean
   */
  @Bean(name = "springSessionDefaultRedisSerializer")
  public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
    return new GenericJackson2JsonRedisSerializer(new ObjectMapper());
  }
}
