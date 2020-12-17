package com.ethan.cache.redis.lock;

import com.ethan.cache.redis.annotation.DistributedLockable;
import com.ethan.cache.redis.handler.LockHandler;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@Deprecated
public class RedisLockClient {
  private final Logger LOGGER = LoggerFactory.getLogger(RedisLockClient.class);

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  /**
   * try lock
   *
   * @author piaoruiqing
   *
   * @param <T>
   * @param key           lock key
   * @param handler       logic you want to execute
   * @param timeout       timeout
   * @param autoUnlock    whether unlock when completed
   * @param retries       number of retries
   * @param waitingTime   retry interval
   * @param onFailure     throw an runtime exception while fail to get lock
   * @return
   */
  public <T> T tryLock(String key, LockHandler<T> handler, long timeout, boolean autoUnlock, int retries, long waitingTime,
                       Class<? extends RuntimeException> onFailure) throws Throwable {

    try (DistributedLock lock = this.acquire(key, timeout, retries, waitingTime);) {
      if (lock != null) {
        LOGGER.info("get lock success, key: {}", key);
        return handler.handle();
      }
      LOGGER.info("get lock fail, key: {}", key);
      if (null != onFailure && onFailure != DistributedLockable.NoException.class) {
        throw onFailure.newInstance();
      }
      return null;
    }
  }

  /**
   * acquire distributed  lock
   *
   * @author piaoruiqing
   *
   * @param key           lock key
   * @param timeout       timeout
   * @param retries       number of retries
   * @param waitingTime   retry interval
   * @return
   * @throws InterruptedException
   */
  public DistributedLock acquire(String key, long timeout, int retries, long waitingTime) throws InterruptedException {

    final String value = RandomStringUtils.randomAlphanumeric(4) + System.currentTimeMillis();
    do {
      Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.MILLISECONDS);
      if (result) {
        return new RedisDistributedLock(stringRedisTemplate, key, value);
      }
      if (retries > NumberUtils.INTEGER_ZERO) {
        TimeUnit.MILLISECONDS.sleep(waitingTime);
      }
      if (Thread.currentThread().isInterrupted()) {
        break;
      }
    } while (retries-- > NumberUtils.INTEGER_ZERO);

    return null;
  }
}
