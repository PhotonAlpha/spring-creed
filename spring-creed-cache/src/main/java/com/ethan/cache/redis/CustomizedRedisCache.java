package com.ethan.cache.redis;

import com.ethan.cache.redis.lock.RedisLock;
import com.ethan.context.utils.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

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

  private final String prefix;

  /**
   * the cache will force refresh before expired
   * time unit: seconds
   */
  private long preloadSecondTime = 0;
  /**
   * the cache expiration time
   */
  private long expirationTime;

  /**
   * force refresh cache, default false
   */
  private boolean forceRefresh = false;


  private CacheSupport getCacheSupport() {
    return SpringContextUtils.getBean(CacheSupport.class);
  }

  public CustomizedRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig,
                                 String prefix, RedisOperations<? extends Object, ? extends Object> redisOperations,
                                 long expirationTime, long preloadSecondTime, boolean forceRefresh) {
    // overwrite the default keyPrefix
    super(name, cacheWriter, cacheConfig.prefixKeysWith(prefix));
    this.prefix = prefix;
    this.redisOperations = redisOperations;
    this.expirationTime = expirationTime;
    this.preloadSecondTime = preloadSecondTime;
    this.forceRefresh = forceRefresh;
  }

  @Override
  public void evict(Object key) {
    super.evict(key);
    redisOperations.delete(getCacheKey(key) + INVOCATION_CACHE_KEY_SUFFIX);
  }

  @Override
  public ValueWrapper get(Object key) {
    String cacheKey = getCacheKey(key);
    ValueWrapper valueWrapper = this.get(cacheKey);
    if (null != valueWrapper && CustomizedRedisCache.this.preloadSecondTime > 0) {
      // refresh the data
    }
    return valueWrapper;
  }

  /**
   * refresh the cache
   * @param key
   * @param cacheKeyStr
   */
  private void refreshCache(Object key, String cacheKeyStr) {
    Long ttl = this.redisOperations.getExpire(cacheKeyStr);
    if (null != ttl && ttl <= CustomizedRedisCache.this.preloadSecondTime) {
      // 判断是否需要强制刷新在开启刷新线程
      if (!isForceRefresh()) {
        softRefresh(cacheKeyStr);
      } else {
        forceRefresh(cacheKeyStr);
      }
    }
  }

  private void softRefresh(String cacheKeyStr) {
    // 加一个分布式锁，只放一个请求去刷新缓存
    RedisLock redisLock = new RedisLock((RedisTemplate<String, Object>) redisOperations, cacheKeyStr + "_lock");
    try {
      if (redisLock.tryLock()) {
        redisOperations.expire(cacheKeyStr, this.expirationSecondTime, TimeUnit.SECONDS);
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    } finally {
      redisLock.unlock();
    }
  }

  /**
   * get RedisCacheKey
   * @param key
   * @return key's value
   */
  public String getCacheKey(Object key) {
    return createCacheKey(key);
  }

  public boolean isForceRefresh() {
    return forceRefresh;
  }
}
