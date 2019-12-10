package com.ethan.cache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

public class CustomizedRedisCache extends RedisCache {
  private static final Logger log = LoggerFactory.getLogger(CustomizedRedisCache.class);

  public static final String INVOCATION_CACHE_KEY_SUFFIX = ":invocation_suffix";
  /**
   * refresh cache retry times
   */
  private static final int RETRY_COUNT = 5;

  private CacheSupport getCacheSupport() {
    return SpringContextUtils.getBean(CacheSupport.class);
  }

  protected CustomizedRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
    super(name, cacheWriter, cacheConfig);
  }
}
