package com.ethan.cache.redis;

import com.ethan.cache.model.RedisCacheBean;
import com.ethan.cache.redis.lock.RedisLock;
import com.ethan.context.utils.SpringContextUtils;
import com.ethan.context.utils.ThreadTaskUtils;
import org.slf4j.Logger;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

public class CustomizedRedisCache extends RedisCache {

  public static final String INVOCATION_CACHE_KEY_SUFFIX = ":invocation_suffix";
  /**
   * refresh cache retry times
   */
  private static final int RETRY_COUNT = 5;
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(CustomizedRedisCache.class);

	ThreadAwaitContainer container = new ThreadAwaitContainer();

  private final RedisOperations redisOperations;

  /**
   * the cache will force refresh before expired
   * time unit: seconds
   */
  private long preloadTime;
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
                              RedisOperations<? extends Object, ? extends Object> redisOperations, RedisCacheBean redisCacheBean) {
    // overwrite the default keyPrefix
    super(name, cacheWriter, cacheConfig);
    this.redisOperations = redisOperations;
    this.preloadTime = redisCacheBean.getPreloadTime();
    this.expirationTime = redisCacheBean.getExpirationTime();
    this.forceRefresh = redisCacheBean.getForceRefresh();
  }

  @Override
  public void evict(Object key) {
    super.evict(key);
    redisOperations.delete(getCacheKey(key) + INVOCATION_CACHE_KEY_SUFFIX);
  }

  @Override
  public ValueWrapper get(Object key) {
    String cacheKeyStr = getCacheKey(key);

    Object value = this.lookup(key);
    ValueWrapper valueWrapper = this.toValueWrapper(value);
    if (null != valueWrapper && CustomizedRedisCache.this.preloadTime > 0) {
      // refresh the data
      refreshCache(key, cacheKeyStr);
    }
    return valueWrapper;
  }

  @Override
  public <T> T get(Object key, Class<T> type) {
    String cacheKeyStr = getCacheKey(key);
    ValueOperations<Object, T> ops = redisOperations.opsForValue();
    T result = ops.get(cacheKeyStr);
    if (result != null) {
      return result;
    }
    RedisLock redisLock = new RedisLock((RedisTemplate<String, Object>) redisOperations, cacheKeyStr + "_sync_lock");
    for (int i = 0; i < RETRY_COUNT; i++) {
      try {
        // 先取缓存，如果有直接返回，没有再去做拿锁操作
        result = ops.get(key);
        if (result != null) {
          return result;
        }

        // 获取分布式锁去后台查询数据
        if (redisLock.lock()) {
          T t = super.get(key, type);
          // 唤醒线程
          container.signalAll(cacheKeyStr);
          return t;
        }
        // 线程等待
        container.await(cacheKeyStr, 20);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      } finally {
        redisLock.unlock();
      }
    }
    return super.get(key, type);
  }

  /**
   * refresh the cache
   * @param key
   * @param cacheKeyStr
   */
  private void refreshCache(Object key, String cacheKeyStr) {
    Long ttl = this.redisOperations.getExpire(cacheKeyStr);
    if (null != ttl && ttl <= CustomizedRedisCache.this.preloadTime) {
      // Determine if you need to force refresh before starting the refresh thread
      if (!isForceRefresh()) {
        softRefresh(cacheKeyStr);
      } else {
        forceRefresh(cacheKeyStr);
      }
    }
  }

  private void softRefresh(String cacheKeyStr) {
    // Add a distributed lock and only put one request to refresh the cache
    RedisLock redisLock = new RedisLock((RedisTemplate<String, Object>) redisOperations, cacheKeyStr);
    try {
      if (redisLock.tryLock()) {
        boolean success = redisOperations.expire(cacheKeyStr, this.expirationTime, TimeUnit.SECONDS);
        log.info("softRefresh expire :{}, expire time is:{}", success, this.expirationTime);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    } finally {
      redisLock.unlock();
    }
  }

  /**
   * force refresh the key
   * @param cacheKeyStr
   */
  private void forceRefresh(String cacheKeyStr) {
    // Start as few threads as possible, because the thread pool is limited
    ThreadTaskUtils.run(() -> {
      // add a distribute lock, only one request to refresh cache
      RedisLock redisLock = new RedisLock((RedisTemplate<String, Object>) redisOperations, cacheKeyStr + "_lock");
      try {
        if (redisLock.lock()) {
          // 获取锁之后再判断一下过期时间，看是否需要加载数据
          // After acquiring the lock, determine the expiration time to see if data needs to be loaded
          Long ttl = CustomizedRedisCache.this.redisOperations.getExpire(cacheKeyStr);
          if (null != ttl && ttl <= CustomizedRedisCache.this.preloadTime) {
            // reload data by poxy
            CustomizedRedisCache.this.getCacheSupport().refreshCacheByKey(CustomizedRedisCache.super.getName(), cacheKeyStr);
          }
        }
      } catch (Exception e) {
        log.info(e.getMessage(), e);
      } finally {
        redisLock.unlock();
      }
    });
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

  public long getExpirationTime() {
    return expirationTime;
  }
}
