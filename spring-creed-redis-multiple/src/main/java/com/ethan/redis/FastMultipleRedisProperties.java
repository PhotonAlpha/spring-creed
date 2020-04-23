package com.ethan.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * redis 多环境配置
 */
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
