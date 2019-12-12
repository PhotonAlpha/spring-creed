package com.ethan.cache;

import com.ethan.cache.config.CaffeineCacheBean;
import com.ethan.cache.config.RedisCacheBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LayeringCacheManager implements CacheManager {
  private static final int DEFAULT_EXPIRE_AFTER_WRITE = 2;
  private static final int DEFAULT_INITIAL_CAPACITY = 5;
  private static final int DEFAULT_MAXIMUM_SIZE = 1_000;

  private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(16);

  private Map<String, CaffeineCacheBean.CaffeineCacheConfiguration> primaryCacheConfig;
  private Map<String, RedisCacheBean.RedisCacheConfiguration> secondaryCacheConfig;
  /**
   * allow dynamic create cache, default true
   */
  private boolean dynamic = true;
  /**
   * 缓存值是否允许为NULL
   */
  private boolean allowNullValues = false;

  @Override
  public Cache getCache(String s) {
    return null;
  }

  @Override
  public Collection<String> getCacheNames() {
    return null;
  }

  public void setPrimaryCacheConfig(Map<String, CaffeineCacheBean.CaffeineCacheConfiguration> primaryCacheConfig) {
    this.primaryCacheConfig = primaryCacheConfig;
  }

  public void setSecondaryCacheConfig(Map<String, RedisCacheBean.RedisCacheConfiguration> secondaryCacheConfig) {
    this.secondaryCacheConfig = secondaryCacheConfig;
  }
}
