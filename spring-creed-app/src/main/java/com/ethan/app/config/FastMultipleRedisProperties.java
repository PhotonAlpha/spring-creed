package com.ethan.app.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import java.util.Map;

public class FastMultipleRedisProperties {
  private Map<String, RedisProperties> redis;

  public Map<String, RedisProperties> getRedis() {
    return redis;
  }

  public void setRedis(Map<String, RedisProperties> redis) {
    this.redis = redis;
  }
}
