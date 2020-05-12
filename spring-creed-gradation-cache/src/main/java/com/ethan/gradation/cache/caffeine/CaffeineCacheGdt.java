package com.ethan.gradation.cache.caffeine;

import com.ethan.gradation.cache.CacheStatsManager;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.caffeine.CaffeineCache;

import java.util.concurrent.Callable;

/**
 * 基于Caffeine实现的一级缓存
 */
public class CaffeineCacheGdt extends CaffeineCache implements CacheStatsManager {
  private final String name;

  private final com.github.benmanes.caffeine.cache.Cache<Object, Object> cache;

  public CaffeineCacheGdt(String name, com.github.benmanes.caffeine.cache.Cache<Object, Object> cache) {
    super(name, cache);
    this.name = name;
    this.cache = cache;
  }

  public CaffeineCacheGdt(String name, com.github.benmanes.caffeine.cache.Cache<Object, Object> cache, boolean allowNullValues) {
    super(name, cache, allowNullValues);
    this.name = name;
    this.cache = cache;
  }

  @Override
  public ValueWrapper get(Object key) {
    return super.get(key);
  }

  @Override
  public <T> T get(Object key, Callable<T> valueLoader) {
    return super.get(key, valueLoader);
  }

  @Override
  public void put(Object key, Object value) {
    super.put(key, value);
  }

  @Override
  public ValueWrapper putIfAbsent(Object key, Object value) {
    return super.putIfAbsent(key, value);
  }

  @Override
  public void evict(Object key) {
    super.evict(key);
  }

  @Override
  public void clear() {
    super.clear();
  }

  @Override
  public <T> T get(Object key, Class<T> type) {
    return super.get(key, type);
  }

  @Override
  public CacheStats getCacheStats() {
    return getNativeCache().stats();
  }

}
