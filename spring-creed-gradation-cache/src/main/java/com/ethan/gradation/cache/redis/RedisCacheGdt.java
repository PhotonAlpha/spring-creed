package com.ethan.gradation.cache.redis;

import com.ethan.context.utils.InstanceUtils;
import com.ethan.context.utils.ThreadTaskUtils;
import com.ethan.gradation.cache.CacheStatsManager;
import com.ethan.gradation.config.RedisCacheProperty;
import com.ethan.gradation.exception.LoaderCacheValueException;
import com.ethan.gradation.support.AwaitThreadContainer;
import com.ethan.gradation.support.RedisLock;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.github.benmanes.caffeine.cache.stats.ConcurrentStatsCounter;
import com.github.benmanes.caffeine.cache.stats.StatsCounter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class RedisCacheGdt extends RedisCache implements CacheStatsManager {
  private Supplier<StatsCounter> statsCounterSupplier = ConcurrentStatsCounter::new;
  private static final ObjectMapper MAPPER = InstanceUtils.getMapperInstance();
  /**
   * 刷新缓存重试次数
   */
  private static final int RETRY_COUNT = 10;

  /**
   * 刷新缓存等待时间，单位毫秒
   */
  private static final long WAIT_TIME = 20;

  /**
   * 等待线程容器
   */
  private AwaitThreadContainer container = new AwaitThreadContainer();

  /**
   * redis 客户端
   */
  private RedisTemplate<String, Object> redisTemplate;

  /**
   * 缓存有效时间,毫秒
   */
  private long expiration;

  /**
   * 缓存主动在失效前强制刷新缓存的时间
   * 单位：毫秒
   */
  private long preloadTime;

  /**
   * 是否强制刷新（执行被缓存的方法），默认是false
   */
  private boolean forceRefresh = false;

  /**
   * 非空值和null值之间的时间倍率，默认是1。allowNullValue=true才有效
   * <p>
   * 如配置缓存的有效时间是200秒，倍率这设置成10，
   * 那么当缓存value为null时，缓存的有效时间将是20秒，非空时为200秒
   * </p>
   */
  private final int magnification;

  /**
   * 是否开启统计
   */
  private boolean stats = false;

  public RedisCacheGdt(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig,
                       RedisTemplate<String, Object> redisTemplate, RedisCacheProperty property) {
    // overwrite the default keyPrefix
    super(name, cacheWriter, cacheConfig);
    this.redisTemplate = redisTemplate;
    this.preloadTime = property.getTimeUnit().toMillis(property.getPreloadTime());
    this.expiration =property.getTimeUnit().toMillis(property.getExpiration());
    this.forceRefresh = property.getForceRefresh();
    this.magnification = property.getMagnification();
    this.stats = property.getStats();
  }

  @Override
  protected Object lookup(Object key) {
    Object result = super.lookup(key);
    if (isStats()) {
      if (result == NullValue.INSTANCE) {
        getStatsCounter().recordMisses(1);
      } else {
        getStatsCounter().recordHits(1);
      }
    }
/**    if (result != null && this.preloadTime > 0) {
      // 刷新缓存
      refreshCache(key, valueLoader, result);
    }*/
    return result;
  }

  @Override
  public synchronized  <T> T get(Object key, Callable<T> valueLoader) {

    String redisKey = createCacheKey(key);
    log.info("redis缓存 key= {} 查询redis缓存如果没有命中，从数据库获取数据", redisKey);
    // 先获取缓存，如果有直接返回
    Boolean hasKey = redisTemplate.hasKey(redisKey);
    if (Boolean.TRUE.equals(hasKey) && this.preloadTime > 0) {
      // 刷新缓存
      Object result = redisTemplate.opsForValue().get(redisKey);
      refreshCache(key, valueLoader, result);
      return (T) result;
    }
    // 执行缓存方法, 当没有数据时，执行retry
    return executeCacheMethod(key, valueLoader);
  }

  @Override
  public void put(Object key, Object value) {
    String redisKey = createCacheKey(key);
    log.info("redis缓存 key= {} put缓存，缓存值：{}", redisKey, value);
    putValue(key, value);
  }

  @Override
  public ValueWrapper putIfAbsent(Object key, Object value) {
    String redisKey = createCacheKey(key);
    log.info("redis缓存 key= {} putIfAbsent缓存，缓存值：{}", redisKey, value);
    Object result = get(key);
    if (result != null) {
      return toValueWrapper(result);
    }
    put(key, value);
    return null;
  }

  @Override
  public void evict(Object key) {
    String redisKey = createCacheKey(key);
    log.info("清除redis缓存 key= {} ", redisKey);
    super.evict(key);
  }

  @Override
  public void clear() {
    super.clear();
  }

  /**
   * 同一个线程循环5次查询缓存，每次等待20毫秒，如果还是没有数据直接去执行被缓存的方法
   */
  private <T> T executeCacheMethod(Object key, Callable<T> valueLoader) {
    String redisKey = createCacheKey(key);
    RedisLock redisLock = new RedisLock(redisTemplate, redisKey + "_sync_lock");
    // 同一个线程循环20次查询缓存，每次等待20毫秒，如果还是没有数据直接去执行被缓存的方法
    for (int i = 0; i < RETRY_COUNT; i++) {
      try {
        // 先取缓存，如果有直接返回，没有再去做拿锁操作
        Object result = redisTemplate.opsForValue().get(redisKey);
        if (result != null) {
          log.debug("redis缓存 key= {} 获取到锁后查询查询缓存命中，不需要执行被缓存的方法", redisKey);
          return (T) fromStoreValue(result);
        }

        // 获取分布式锁去后台查询数据
        if (redisLock.lock()) {
          T t = loaderAndPutValue(key, valueLoader, true);
          log.debug("redis缓存 key= {} 从数据库获取数据完毕，唤醒所有等待线程", redisKey);
          // 唤醒线程
          container.signalAll(redisKey);
          return t;
        }
        // 线程等待
        log.debug("redis缓存 key= {} 从数据库获取数据未获取到锁，进入等待状态，等待{}毫秒", redisKey, WAIT_TIME);
        container.await(redisKey, WAIT_TIME);
      } catch (Exception e) {
        container.signalAll(redisKey);
        throw new LoaderCacheValueException(redisKey, e);
      } finally {
        redisLock.unlock();
      }
    }
    log.debug("redis缓存 key={} 等待{}次，共{}毫秒，任未获取到缓存，直接去执行被缓存的方法", redisKey, RETRY_COUNT, RETRY_COUNT * WAIT_TIME, WAIT_TIME);
    return loaderAndPutValue(key, valueLoader, true);
  }

  /**
   * 加载并将数据放到redis缓存
   */
  private <T> T loaderAndPutValue(Object key, Callable<T> valueLoader, boolean isLoad) {
    long start = System.currentTimeMillis();
    if (isLoad && isStats()) {
      getStatsCounter().recordHits(1);
    }
    String redisKey = createCacheKey(key);
    try {
      // 加载数据
      Object result = putValue(key, valueLoader.call());
      log.debug("redis缓存 key={} 执行被缓存的方法，并将其放入缓存, 耗时：{}。数据:{}", redisKey, System.currentTimeMillis() - start, MAPPER.writeValueAsString(result));

      if (isLoad && isStats()) {
        getStatsCounter().recordLoadSuccess(System.currentTimeMillis() - start);
      }
      return (T) fromStoreValue(result);
    } catch (Exception e) {
      throw new LoaderCacheValueException(redisKey, e);
    }
  }

  private Object putValue(Object key, Object value) {
    String redisKey = createCacheKey(key);
    Object result = toStoreValue(value);
    // redis 缓存不允许直接存NULL，如果结果返回NULL需要删除缓存
    if (result == NullValue.INSTANCE) {
      redisTemplate.delete(redisKey);
      return result;
    }
    // 不允许缓存NULL值，删除缓存
    if (!isAllowNullValues() && result instanceof NullValue) {
      redisTemplate.delete(redisKey);
      return result;
    }

    // 允许缓存NULL值
    long expirationTime = this.expiration;
    // 允许缓存NULL值且缓存为值为null时需要重新计算缓存时间
    if (isAllowNullValues() && result instanceof NullValue) {
      expirationTime = expirationTime / getMagnification();
    }
    // 将数据放到缓存
    redisTemplate.opsForValue().set(redisKey, result, expirationTime, TimeUnit.MILLISECONDS);
    return result;
  }

  /**
   * 刷新缓存数据
   */
  private <T> void refreshCache(Object key, Callable<T> valueLoader, Object result) {
    String redisKey = createCacheKey(key);
    Long ttl = redisTemplate.getExpire(redisKey);
    Long preload = preloadTime;
    // 允许缓存NULL值，则自动刷新时间也要除以倍数
    boolean flag = isAllowNullValues() && (result instanceof NullValue || result == null);
    if (flag) {
      preload = preload / getMagnification();
    }
    if (ttl > 0 && TimeUnit.SECONDS.toMillis(ttl) <= preload) {
      // 判断是否需要强制刷新在开启刷新线程
      if (!isForceRefresh()) {
        log.info("redis缓存 key={} 软刷新缓存模式", redisKey);
        softRefresh(key);
      } else {
        log.info("redis缓存 key={} 强刷新缓存模式", redisKey);
        forceRefresh(key, valueLoader);
      }
    }
  }

  /**
   * 软刷新，直接修改缓存时间
   *
   * @param key 键
   */
  private void softRefresh(Object key) {
    String redisKey = createCacheKey(key);
    // 加一个分布式锁，只放一个请求去刷新缓存
    RedisLock redisLock = new RedisLock(redisTemplate, redisKey + "_lock");
    try {
      if (redisLock.tryLock()) {
        redisTemplate.expire(redisKey, this.expiration, TimeUnit.MILLISECONDS);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    } finally {
      redisLock.unlock();
    }
  }

  /**
   * 硬刷新（执行被缓存的方法）
   *
   * @param key 键
   * @param valueLoader   数据加载器
   */
  private <T> void forceRefresh(Object key, Callable<T> valueLoader) {
    String redisKey = createCacheKey(key);
    // 尽量少的去开启线程，因为线程池是有限的
    ThreadTaskUtils.run(() -> {
      // 加一个分布式锁，只放一个请求去刷新缓存
      RedisLock redisLock = new RedisLock(redisTemplate, redisKey + "_lock");
      try {
        if (redisLock.lock()) {
          // 获取锁之后再判断一下过期时间，看是否需要加载数据
          Long ttl = redisTemplate.getExpire(redisKey);
          if (null != ttl && ttl > 0 && TimeUnit.SECONDS.toMillis(ttl) <= preloadTime) {
            // 加载数据并放到缓存
            loaderAndPutValue(key, valueLoader, false);
          }
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      } finally {
        redisLock.unlock();
      }
    });
  }

  public StatsCounter getStatsCounter() {
    return statsCounterSupplier.get();
  }

  @Override
  public CacheStats getCacheStats() {
    return getStatsCounter().snapshot();
  }

  public void setStatsCounter(Supplier<StatsCounter> statsCounterSupplier) {
    this.statsCounterSupplier = statsCounterSupplier;
  }

  public boolean isStats() {
    return stats;
  }

  public int getMagnification() {
    return magnification;
  }

  public boolean isForceRefresh() {
    return forceRefresh;
  }

}
