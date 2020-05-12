package com.ethan.gradation.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

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
 *
 */
@Slf4j
public class RedisLock {
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 调用set后的返回值
     */
    private static final String OK = "OK";

    /**
     * 默认请求锁的超时时间(ms 毫秒)
     */
    private static final long TIME_OUT = 100;

    /**
     * 默认锁的有效时间(s)
     */
    private static final int EXPIRE = 60;

    /**
     * 解锁的lua脚本
     */
    private static final String UNLOCK_LUA;

    static {
        StringBuilder sb = new StringBuilder("if redis.call(\"get\", KEYS[1]) == ARGV[1] ");
        sb.append("then ")
            .append("    return redis.call(\"del\", KEYS[1]) ")
            .append("else ")
            .append("    return 0 ")
            .append("end ");
        UNLOCK_LUA = sb.toString();
    }

    /**
     * 锁标志对应的key
     */
    private String lockKey;

    /**
     * 记录到日志的锁标志对应的key
     */
    private String lockKeyLog = "";

    /**
     * 锁对应的值
     */
    private String lockValue;

    /**
     * 锁的有效时间(s)
     */
    private int expireTime = EXPIRE;

    /**
     * 请求锁的超时时间(ms)
     */
    private long timeOut = TIME_OUT;

    /**
     * 锁标记
     */
    private volatile boolean locked = false;

    private final Random random = new Random();

    /**
     * 使用默认的锁过期时间和请求锁的超时时间
     *
     * @param redisTemplate redis客户端
     * @param lockKey       锁的key（Redis的Key）
     */
    public RedisLock(RedisTemplate<String, Object> redisTemplate, String lockKey) {
        this.redisTemplate = redisTemplate;
        this.lockKey = lockKey + "_lock";
    }

    /**
     * 使用默认的请求锁的超时时间，指定锁的过期时间
     *
     * @param redisTemplate redis客户端
     * @param lockKey       锁的key（Redis的Key）
     * @param expireTime    锁的过期时间(单位：秒)
     */
    public RedisLock(RedisTemplate<String, Object> redisTemplate, String lockKey, int expireTime) {
        this(redisTemplate, lockKey);
        this.expireTime = expireTime;
    }

    /**
     * 使用默认的锁的过期时间，指定请求锁的超时时间
     *
     * @param redisTemplate redis客户端
     * @param lockKey       锁的key（Redis的Key）
     * @param timeOut       请求锁的超时时间(单位：毫秒)
     */
    public RedisLock(RedisTemplate<String, Object> redisTemplate, String lockKey, long timeOut) {
        this(redisTemplate, lockKey);
        this.timeOut = timeOut;
    }

    /**
     * 锁的过期时间和请求锁的超时时间都是用指定的值
     *
     * @param redisTemplate redis客户端
     * @param lockKey       锁的key（Redis的Key）
     * @param expireTime    锁的过期时间(单位：秒)
     * @param timeOut       请求锁的超时时间(单位：毫秒)
     */
    public RedisLock(RedisTemplate<String, Object> redisTemplate, String lockKey, int expireTime, long timeOut) {
        this(redisTemplate, lockKey, expireTime);
        this.timeOut = timeOut;
    }

    /**
     * 尝试获取锁 超时返回
     *
     * @return boolean
     */
    public boolean tryLock() {
        // 生成随机key
        this.lockValue = getUniqueKey();
        // 请求锁超时时间，纳秒
        long timeout = timeOut * 1000000;
        // 系统当前时间，纳秒
        long nowTime = System.nanoTime();
        while ((System.nanoTime() - nowTime) < timeout) {
            if (this.set(lockKey, lockValue, expireTime)) {
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
        this.lockValue = getUniqueKey();
        //不存在则添加 且设置过期时间（单位ms）
        locked = set(lockKey, lockValue, expireTime);
        return locked;
    }

    /**
     * 以阻塞方式的获取锁
     *
     * @return 是否成功获得锁
     */
    public boolean lockBlock() {
        this.lockValue = getUniqueKey();
        while (true) {
            //不存在则添加 且设置过期时间（单位ms）
            locked = set(lockKey, lockValue, expireTime);
            if (locked) {
                return locked;
            }
            // 每次请求等待一段时间
            seleep(10, 50000);
        }
    }

    /**
     * 解锁
     * <p>
     * 可以通过以下修改，让这个锁实现更健壮：
     * <p>
     * 不使用固定的字符串作为键的值，而是设置一个不可猜测（non-guessable）的长随机字符串，作为口令串（token）。
     * 不使用 DEL 命令来释放锁，而是发送一个 Lua 脚本，这个脚本只在客户端传入的值和键的口令串相匹配时，才对键进行删除。
     * 这两个改动可以防止持有过期锁的客户端误删现有锁的情况出现。
     *
     * @return Boolean
     */
    public Boolean unlock() {
        // 只有加锁成功并且锁还有效才去释放锁
        // 只有加锁成功并且锁还有效才去释放锁
        if (locked) {
            try {
                RedisScript<Long> script = RedisScript.of(UNLOCK_LUA, Long.class);
                List<String> keys = new ArrayList<>();
                keys.add(lockKey);
                Long result = redisTemplate.execute(script, keys, lockValue);
                if (result == 0 && !StringUtils.isEmpty(lockKeyLog)) {
                    log.debug("Redis分布式锁，解锁{}失败！解锁时间：{}", lockKeyLog, System.currentTimeMillis());
                }

                locked = result == 0;
                return result == 1;
            } catch (Throwable e) {
                log.warn("Redis不支持EVAL命令，使用降级方式解锁：{}", e.getMessage());
                String value = this.get(lockKey, String.class);
                if (lockValue.equals(value)) {
                    redisTemplate.delete(lockKey);
                    return true;
                }
                return false;
            }
        }

        return true;
    }

    /**
     * 重写redisTemplate的set方法
     * <p>
     * 命令 SET resource-name anystring NX EX max-lock-time 是一种在 Redis 中实现锁的简单方法。
     * <p>
     * 客户端执行以上的命令：
     * <p>
     * 如果服务器返回 OK ，那么这个客户端获得锁。
     * 如果服务器返回 NIL ，那么客户端获取锁失败，可以在稍后再重试。
     *
     * @param key     锁的Key
     * @param value   锁里面的值
     * @param seconds 过去时间（秒）
     * @return String
     */
    private boolean set(final String key, final String value, final long seconds) {
        Assert.isTrue(!StringUtils.isEmpty(key), "key不能为空");
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
        if (!StringUtils.isEmpty(lockKeyLog) && Objects.nonNull(success) && success) {
            log.debug("获取锁{}的时间：{}", lockKeyLog, System.currentTimeMillis());
        }
        return Objects.nonNull(success) && success;
    }

    /**
     * 获取redis里面的值
     *
     * @param key    key
     * @param aClass class
     * @return T
     */
    private <T> T get(final String key, Class<T> aClass) {
        Assert.isTrue(!StringUtils.isEmpty(key), "key不能为空");
        return (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取锁状态
     *
     * @return boolean
     * @author yuhao.wang
     */
    public boolean isLock() {

        return locked;
    }

    /**
     * @param millis 毫秒
     * @param nanos  纳秒
     * @Title: seleep
     * @Description: 线程等待时间
     * @author yuhao.wang
     */
    private void seleep(long millis, int nanos) {
        try {
            Thread.sleep(millis, random.nextInt(nanos));
        } catch (Exception e) {
            log.debug("获取分布式锁休眠被中断：", e);
        }
    }

    /**
     * 生成高效随机数
     * @return
     */
    private String getUniqueKey() {
        //lockValue = UUID.randomUUID().toString();
        // UUID.randomUUID() have Poor performance in high concurrency situations
        return RandomStringUtils.randomAlphabetic(4) + System.nanoTime();
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