package com.ethan.cache.dao;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.Resource;

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
