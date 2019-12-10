package com.ethan.cache;

import com.ethan.cache.redis.CustomizedRedisCache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisOperations;

import java.util.concurrent.Callable;

public class LayeringCache extends AbstractValueAdaptingCache {
  /**
   * cache name
   */
  private final String name;

  private boolean enablePrimaryCache = true;

  private final CaffeineCache caffeineCache;

  RedisOperations<? extends Object, ? extends Object> redisOperations;

  /**
   *
   * @param allowNullValues allow nullable, default is false
   * @param prefix
   * @param redisOperations
   * @param expiration  redis expire time
   * @param preloadSecondTime redis auto refresh idle time
   * @param name  cache name
   * @param enablePrimaryCache enable the caffeine cache, default false
   * @param forceRefresh  force refresh(reload from database)
   * @param caffeineCache caffeine cache
   */
  public LayeringCache(boolean allowNullValues,byte[] prefix, RedisOperations<? extends Object, ? extends Object> redisOperations,
                       long expiration, long preloadSecondTime, String name, boolean enablePrimaryCache,boolean forceRefresh,
                       com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache) {
    super(allowNullValues);
    this.name = name;
    this.enablePrimaryCache = enablePrimaryCache;
    this.redisOperations = redisOperations;
    this.redisCache = new CustomizedRedisCache(name, prefix, redisOperations, expiration, preloadSecondTime, forceRefresh, allowNullValues);
    this.caffeineCache = new CaffeineCache(name, caffeineCache, allowNullValues);
  }

  @Override
  protected Object lookup(Object o) {
    return null;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Object getNativeCache() {
    return null;
  }

  @Override
  public <T> T get(Object o, Callable<T> callable) {
    return null;
  }

  @Override
  public void put(Object o, Object o1) {

  }

  @Override
  public ValueWrapper putIfAbsent(Object o, Object o1) {
    return null;
  }

  @Override
  public void evict(Object o) {

  }

  @Override
  public void clear() {

  }
}
