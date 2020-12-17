package com.ethan.redis.multiple;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * redis 多环境配置
 * {@link spring-boot-autoconfigure-2.1.9.RELEASE.jar#META-INF#spring-configuration-metadata.json}
 */
@ConfigurationProperties("multi")
public class FastMultipleRedisProperties {
  @NestedConfigurationProperty
  private Map<String, RedisProperties> redis;

  public Map<String, RedisProperties> getRedis() {
    return redis;
  }

  public void setRedis(Map<String, RedisProperties> redis) {
    this.redis = redis;
  }
}
