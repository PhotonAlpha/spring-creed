package com.ethan.cache.redis.lock;

import io.lettuce.core.SetArgs;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.dynamic.Commands;
import io.lettuce.core.dynamic.RedisCommandFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisKeyCommands;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

/**
 * Redis分布式锁
 * 使用 SET resource-name anystring NX EX max-lock-time 实现
 * <p>
 * 该方案在 Redis 官方 SET 命令页有详细介绍。
 * http://doc.redisfans.com/string/set.html
 * <p>
 * 在介绍该分布式锁设计之前，我们先来看一下在从 Redis 2.6.12 开始 SET 提供的新特性，
 * 命令 SET key value [EX seconds] [PX milliseconds] [NX|XX]，其中：
 * <p>
 * EX seconds — 以秒为单位设置 key 的过期时间；
 * PX milliseconds — 以毫秒为单位设置 key 的过期时间；
 * NX — 将key 的值设为value ，当且仅当key 不存在，等效于 SETNX。
 * XX — 将key 的值设为value ，当且仅当key 存在，等效于 SETEX。
 * <p>
 * 命令 SET resource-name anystring NX EX max-lock-time 是一种在 Redis 中实现锁的简单方法。
 * <p>
 * 客户端执行以上的命令：
 * <p>
 * 如果服务器返回 OK ，那么这个客户端获得锁。
 * 如果服务器返回 NIL ，那么客户端获取锁失败，可以在稍后再重试。
 */
@Slf4j
public class RedisLock {
  private RedisTemplate<String, Object> redisTemplate;

  /**
   * set the key as value ，if and only if key is null, which is equivalent to 'SETNX'
   */
  public static final String NX = "NX";

  /**
   * seconds — Set the expiration time of the key in seconds, which is equivalent to 'SETEX key second value'
   */
  public static final String EX = "EX";

  /**
   * the return value after set success
   */
  public static final String OK = "OK";

  /**
   * 默认请求锁的超时时间(ms 毫秒)
   */
  private static final long TIME_OUT = 100;

  /**
   * the default lock effective time(s)
   */
  public static final int EXPIRE = 60;

  /**
   * unlock lua script
   */
  public static final String UNLOCK_LUA;

  static {
    StringBuilder sb = new StringBuilder();
    sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
    sb.append("then ");
    sb.append("    return redis.call(\"del\",KEYS[1]) ");
    sb.append("else ");
    sb.append("    return 0 ");
    sb.append("end ");
    UNLOCK_LUA = sb.toString();
  }

  /**
   * lockKey
   */
  private String lockKey;

  /**
   * the lock key which is recorded in log
   */
  private String lockKeyLog = "";

  /**
   * lockValue
   */
  private String lockValue;

  /**
   * lock effective time(s)
   */
  private int expireTime = EXPIRE;

  /**
   * the max time to request the lock(ms)
   */
  private long timeOut = TIME_OUT;

  /**
   * locke flag
   */
  private volatile boolean locked = false;

  final Random random = new Random();

  /**
   * use the default lock effective time and request await time
   * @param redisTemplate
   * @param lockKey lockKey(equals redis key)
   */
  public RedisLock(RedisTemplate<String, Object> redisTemplate, String lockKey) {
    this.redisTemplate = redisTemplate;
    this.lockKey = lockKey + "_lock";
  }

  /**
   * use the default request await time and customise lock effective time
   * @param redisTemplate
   * @param lockKey       lockKey(equals redis key)
   * @param expireTime    lock effective time(s)
   */
  public RedisLock(RedisTemplate<String, Object> redisTemplate, String lockKey, int expireTime) {
    this(redisTemplate, lockKey);
    this.expireTime = expireTime;
  }

  /**
   * use the default lock effective time and customise request await time
   * @param redisTemplate
   * @param lockKey       lockKey(equals redis key)
   * @param timeOut       request await time(s)
   */
  public RedisLock(RedisTemplate<String, Object> redisTemplate, String lockKey, long timeOut) {
    this(redisTemplate, lockKey);
    this.timeOut = timeOut;
  }

  /**
   * customise the lock effective time and request await time
   * @param redisTemplate
   * @param lockKey       lockKey(equals redis key)
   * @param expireTime    lock effective time(s)
   * @param timeOut       request await time(s)
   */
  public RedisLock(RedisTemplate<String, Object> redisTemplate, String lockKey, int expireTime, long timeOut) {
    this(redisTemplate, lockKey, expireTime);
    this.timeOut = timeOut;
  }

  /**
   * try to get the lock, return when timeout
   * @return can unlock
   */
  public boolean tryLock() {
    // generate a random key
    lockValue = UUID.randomUUID().toString();
    // request timeout unit, ns
    long timeout = timeOut * 1000000;
    // system current nano times, ns
    long nowTime = System.nanoTime();
    while ((System.nanoTime() - nowTime) < timeout) {
      if (OK.equalsIgnoreCase(this.set(lockKey, lockValue, expireTime))) {
        locked = true;
        // 上锁成功结束请求
        return locked;
      }

      // 每次请求等待一段时间
      seleep(10, 50000);
    }
    return locked;
  }

  /**
   * 尝试获取锁 立即返回
   *
   * @return 是否成功获得锁
   */
  public boolean lock() {
    lockValue = UUID.randomUUID().toString();
    //不存在则添加 且设置过期时间（单位ms）
    String result = set(lockKey, lockValue, expireTime);
    locked = OK.equalsIgnoreCase(result);
    return locked;
  }

  /**
   * 以阻塞方式的获取锁
   *
   * @return 是否成功获得锁
   */
  public boolean lockBlock() {
    lockValue = UUID.randomUUID().toString();
    while (true) {
      //不存在则添加 且设置过期时间（单位ms）
      String result = set(lockKey, lockValue, expireTime);
      if (OK.equalsIgnoreCase(result)) {
        locked = true;
        return locked;
      }

      // 每次请求等待一段时间
      sleep(10, 50000);
    }
  }

  /**
   * overwrite the redisTemplate set method
   * <p>
   * command: SET resource-name anystring NX EX max-lock-time is a common way to implement the redis lock
   * <p>
   * if client execute the upper command:
   * <p>
   * if return OK, then get lock success
   * if return NIL, then get lock failure, try it later
   * @param key     lockKey
   * @param value   lockValue
   * @param seconds time pass (s)
   * @return
   */
  private String set(final String key, final String value, final long seconds) {
    Assert.isTrue(!StringUtils.isEmpty(key), "key can not be null");
    return redisTemplate.execute((RedisCallback<String>) connection -> {
      Object nativeConnection = connection.getNativeConnection();
      String result = null;
      if (nativeConnection instanceof RedisAsyncCommands) {
        SetArgs exArgs = SetArgs.Builder.nx().ex(Expiration.from(Duration.ofSeconds(seconds)).getExpirationTime());
        result = ((RedisAsyncCommands) nativeConnection)
            .getStatefulConnection()
            .sync()
            .set(key, value, exArgs);
        if (StringUtils.isNotEmpty(lockKeyLog) && StringUtils.isNotEmpty(result)) {
          log.info("get lock{} cost：{}", lockKeyLog, System.currentTimeMillis());
        }
      }
      return result;
    });
  }

  /**
   * get the lock status
   */
  public boolean isLock() {
    return locked;
  }

  /**
   *
   * @param millis
   * @param nanos
   */
  private void sleep(long millis, int nanos) {
    try {
      Thread.sleep(millis, random.nextInt(nanos));
    } catch (InterruptedException e) {
      log.error("interrupt by sleep thread", e);
    }
  }

  public String getLockKeyLog() {
    return lockKeyLog;
  }

  public void setLockKeyLog(String lockKeyLog) {
    this.lockKeyLog = lockKeyLog;
  }

  public int getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(int expireTime) {
    this.expireTime = expireTime;
  }

  public long getTimeOut() {
    return timeOut;
  }

  public void setTimeOut(long timeOut) {
    this.timeOut = timeOut;
  }
}
