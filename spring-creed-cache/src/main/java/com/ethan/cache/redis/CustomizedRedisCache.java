package com.ethan.cache.redis;

import com.ethan.context.utils.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisOperations;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class CustomizedRedisCache extends RedisCache {
  private static final Logger log = LoggerFactory.getLogger(CustomizedRedisCache.class);


  public static final String INVOCATION_CACHE_KEY_SUFFIX = ":invocation_suffix";
  /**
   * refresh cache retry times
   */
  private static final int RETRY_COUNT = 5;

  ThreadAwaitContainer container = new ThreadAwaitContainer();

  private final RedisOperations redisOperations;

  private final byte[] prefix;

  /**
   * the cache will force refresh before expired
   * time unit: seconds
   */
  private long preloadSecondTime = 0;
  /**
   * the cache expire time
   */
  private long expirationSecondTime;

  /**
   * force refresh cache, default false
   */
  private boolean forceRefresh = false;


  private CacheSupport getCacheSupport() {
    return SpringContextUtils.getBean(CacheSupport.class);
  }

  public CustomizedRedisCache(String name, byte[] prefix, long preloadSecondTime, long expirationSecondTime,
                              RedisOperations<? extends Object, ? extends Object> redisOperations, boolean forceRefresh, boolean allowNullValues) {
    RedisCacheConfiguration cacheConfig = new RedisCacheConfiguration(TimeUnit.SECONDS, allowNullValues, );

    super(name, prefix, redisOperations, expirationSecondTime, allowNullValues);
    this.redisOperations = redisOperations;
    this.prefix = prefix;
    this.preloadSecondTime = preloadSecondTime;
    this.expirationSecondTime = expirationSecondTime;
    this.forceRefresh = forceRefresh;
  }

  protected CustomizedRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
    super(name, cacheWriter, cacheConfig);
  }

  private static RedisCacheConfiguration createCacheConfiguration(long expirationSecondTime, String prefix, boolean allowNullValues) {
    new RedisCacheConfiguration(Duration.ofSeconds(expirationSecondTime), allowNullValues)
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofSeconds(expirationSecondTime))
        .prefixKeysWith(prefix);
  }

}
