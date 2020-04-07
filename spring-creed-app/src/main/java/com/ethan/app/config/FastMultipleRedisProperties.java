package com.ethan.app.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty("multi.redis")
@ConfigurationProperties("multi")
public class FastMultipleRedisProperties {
  private Map<String, RedisProperties> redis;

  public Map<String, RedisProperties> getRedis() {
    return redis;
  }

  public void setRedis(Map<String, RedisProperties> redis) {
    this.redis = redis;
  }
}
