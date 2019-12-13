package com.ethan.cache;

import com.ethan.cache.config.CacheProperties;
import com.ethan.cache.listener.CaffeineRemovalListener;
import com.ethan.cache.model.CaffeineCacheBean;
import com.ethan.cache.model.LayeringBean;
import com.ethan.cache.model.RedisCacheBean;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * multi-level cache (redis caffeine) should share same cache name.
 */
public class LayeringCacheManager implements CacheManager {
  private static final int DEFAULT_EXPIRE_AFTER_WRITE = 2;
  private static final int DEFAULT_INITIAL_CAPACITY = 5;
  private static final int DEFAULT_MAXIMUM_SIZE = 1_000;

  private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);
  private final Map<String, LayeringBean> settings;

  /**
   * allow dynamic create cache, default true
   */
  private boolean dynamic = true;
  /**
   * allowNullValues for redis cache
   */
  private boolean allowNullValues = false;

  private final RedisOperations redisOperations;
  private final RedisConnectionFactory connectionFactory;

  private boolean usePrefix = true;
  private CacheKeyPrefix cachePrefix = CacheKeyPrefix.simple();
  /**
   * redis default expiration time, 0 is never expired
   */
  private long defaultExpiration = 0;

  public LayeringCacheManager(RedisOperations redisOperations,RedisConnectionFactory connectionFactory, CacheProperties cacheProperties) {
    this(redisOperations, connectionFactory, cacheProperties, false);
  }
  public LayeringCacheManager(RedisOperations redisOperations,RedisConnectionFactory connectionFactory, CacheProperties cacheProperties, boolean allowNullValues) {
    Assert.notNull(cacheProperties, "CacheProperties can not be null");
    this.allowNullValues = allowNullValues;
    this.redisOperations = redisOperations;
    this.connectionFactory = connectionFactory;
    settings = cacheProperties.getConfig().stream().collect(Collectors.toMap(LayeringBean::getCacheName, v -> v, (v1, v2) -> v1));
    setCacheNames(cacheProperties);
  }

  /**
   * Initialize a group of caches when initializing the CacheManager.
   * Using this method will initialize a set of caches when the CacheManager is initialized,
   * and will not create more caches at runtime.
   * After using an empty collection or re-specifying dynamic in the configuration,
   * the cache can be created dynamically at runtime again.
   * @param cacheProperties
   */
  public void setCacheNames(CacheProperties cacheProperties) {
    List<LayeringBean> layering = Optional.ofNullable(cacheProperties)
        .map(CacheProperties::getConfig).orElse(Collections.emptyList());
    for (LayeringBean lay : layering) {
      this.cacheMap.put(lay.getCacheName(), createCache(lay.getCacheName()));
    }
    this.dynamic = layering.isEmpty();
  }

  protected Cache createCache(String name) {
    LayeringBean layering = settings.get(name);
    final RedisCacheBean redisBean = layering.getRedis();
    final CaffeineCacheBean caffeineBean = layering.getCaffeine();

    return new LayeringCache(isAllowNullValues(), (usePrefix ? cachePrefix.compute(name) : null), redisOperations, connectionFactory,
        redisFunction(redisBean, RedisCacheBean::getExpirationTime), redisFunction(redisBean, RedisCacheBean::getPreloadTime),
        name, redisFunction(redisBean, RedisCacheBean::getEnablePrimaryCache), redisFunction(redisBean, RedisCacheBean::getForceRefresh),
        createNativeCaffeineCache(caffeineBean));
  }

  /**
   * create the caffeine cache configuration
   * @param bean
   * @return
   */
  protected com.github.benmanes.caffeine.cache.@NonNull Cache<Object, Object> createNativeCaffeineCache(CaffeineCacheBean bean) {
    return Caffeine.newBuilder()
        .initialCapacity(bean.getInitialCapacity())
        .maximumSize(bean.getMaximumSize())
        .expireAfterWrite(bean.getExpireAfterWriteMins(), TimeUnit.SECONDS)
        //.weakKeys()
        .weakValues()
        .removalListener(new CaffeineRemovalListener())
        .recordStats()
        .build();
  }

  private  <R>R redisFunction(RedisCacheBean bean, Function<? super RedisCacheBean, R> claimsResolver) {
    return claimsResolver.apply(bean);
  }

  @Override
  public Cache getCache(String name) {
    Cache cache = this.cacheMap.get(name);
    if (cache == null && this.dynamic) {
      synchronized (this.cacheMap) {
        cache = this.cacheMap.get(name);
        if (cache == null) {
          cache = createCache(name);
          this.cacheMap.put(name, cache);
        }
      }
    }
    return cache;
  }

  @Override
  public Collection<String> getCacheNames() {
    return Collections.unmodifiableSet(this.cacheMap.keySet());
  }

  public Map<String, LayeringBean> getLayeringCacheSettings(CacheProperties cacheProperties) {
    return cacheProperties.getConfig().stream().collect(Collectors.toMap(LayeringBean::getCacheName, v -> v, (v1, v2) -> v2));
  }

  public boolean isAllowNullValues() {
    return allowNullValues;
  }

  public void setAllowNullValues(boolean allowNullValues) {
    if (this.allowNullValues != allowNullValues) {
      this.allowNullValues = allowNullValues;
      refreshKnownCaches();
    }
  }
  /**
   * Recreates cache using the current state of the CacheManager.
   */
  private void refreshKnownCaches() {
    for (Map.Entry<String, Cache> entry : this.cacheMap.entrySet()) {
      entry.setValue(createCache(entry.getKey()));
    }
  }

  public boolean isUsePrefix() {
    return usePrefix;
  }
  /**
   * Whether to use the cache name as a cache prefix when generating keys.
   * The default is true, but it is recommended to set to true.
   * @param usePrefix
   */
  public void setUsePrefix(boolean usePrefix) {
    this.usePrefix = usePrefix;
  }

  public Map<String, LayeringBean> getSettings() {
    return settings;
  }
}
