package com.ethan.cache.dao;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;


public abstract class AbstractBaseRedisDao<K, V> {
  @Resource(name = "redisTemplate")
  protected RedisTemplate<K, V> redisTemplate;

  public void setRedisTemplate(RedisTemplate<K, V> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  protected RedisSerializer<String> getRedisSerializer() {
    return redisTemplate.getStringSerializer();
  }
}
