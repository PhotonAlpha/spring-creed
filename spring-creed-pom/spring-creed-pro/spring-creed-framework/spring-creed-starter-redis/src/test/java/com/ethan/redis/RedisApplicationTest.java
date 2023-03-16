/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.redis;

import com.ethan.redis.service.RedisCacheUserService;
import com.ethan.redis.service.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@SpringBootTest(classes = RedisApplication.class)
public class RedisApplicationTest {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void test001() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "10010");
        map.put("name", "redis_name");
        map.put("amount", 12.34D);
        map.put("age", 11);

/*         String key = "hashKey";
        BoundValueOperations<String, Object> boundValueOperations = redisTemplate.boundValueOps(key);
        boundValueOperations.set(map);
        boundValueOperations.expire(2, TimeUnit.MINUTES); */

        redisTemplate.opsForHash().putAll("hashKey", map);
        // HGET key field 获取存储在哈希表中指定字段的值
        String name = (String) redisTemplate.opsForHash().get("hashKey", "name");
        System.out.println(name);
        // HGET key field
        Double amount = (Double) redisTemplate.opsForHash().get("hashKey", "amount");
        System.out.println(amount);
        // HGETALL key 获取在哈希表中指定 key 的所有字段和值
        Map<Object, Object> map2 = redisTemplate.opsForHash().entries("hashKey");
        System.out.println(map2);
        // HKEYS key 获取在哈希表中指定 key 的所有字段
        Set<Object> keySet = redisTemplate.opsForHash().keys("hashKey");
        System.out.println(keySet);
        // HVALS key 获取在哈希表中指定 key 的所有值
        List<Object> valueList = redisTemplate.opsForHash().values("hashKey");
        System.out.println(valueList);



    }

    @Test
    void expire() {
        Boolean successfull = redisTemplate.expire("hashKey", Duration.ofMinutes(1));
        System.out.println(successfull);
    }

    @Resource
    private RedisCacheUserService redisCacheUserService;
    @Resource
    private CacheManager cacheManager;

    @Test
    void testCache() {
        User user1 = new User("001", "ming", BigDecimal.TEN, 18);
        User user2 = new User("002", "hang", BigDecimal.ONE, 21);
        User user3 = new User("003", "on", BigDecimal.ZERO, 19);
        User user4 = new User("004", "crisp", BigDecimal.TEN, 20);

        Boolean hasKey = redisTemplate.hasKey("user::001");
        log.info("user::001 hasKey:{}", hasKey);

        redisCacheUserService.insertUser(user1);

        hasKey = redisTemplate.hasKey("user::001");
        log.info("user::001 hasKey:{}", hasKey);

        hasKey = redisTemplate.hasKey("user::002");
        log.info("user::002 hasKey:{}", hasKey);

        redisCacheUserService.insertUser(user2);

        hasKey = redisTemplate.hasKey("user::002");
        log.info("user::002 hasKey:{}", hasKey);

        hasKey = redisTemplate.hasKey("user::003");
        log.info("user::003 hasKey:{}", hasKey);

        redisCacheUserService.insertUserWithCondition(user3);

        hasKey = redisTemplate.hasKey("user::003");
        log.info("user::003 hasKey:{}", hasKey);


        hasKey = redisTemplate.hasKey("user::004");
        log.info("user::004 hasKey:{}", hasKey);

        redisCacheUserService.insertUserWithCondition(user4);

        hasKey = redisTemplate.hasKey("user::004");
        log.info("user::004 hasKey:{}", hasKey);

        redisCacheUserService.selectUserById("001");
        redisCacheUserService.selectUserById("002");
        redisCacheUserService.selectUserById("003");
        redisCacheUserService.selectUserById("004");

        hasKey = redisTemplate.hasKey("user::list");
        log.info("List hasKey:{}", hasKey);

        List<User> users = redisCacheUserService.selectUser();

        hasKey = redisTemplate.hasKey("user::list");
        log.info("List hasKey:{} users:{}", hasKey, users);

        users = redisCacheUserService.selectUser();

        hasKey = redisTemplate.hasKey("user::list");
        log.info("List hasKey:{} users:{}", hasKey, users);

        log.info("==========case 1===========");
        redisCacheUserService.deleteUserById("001");
        redisCacheUserService.insertUserWithCondition(user1);

        hasKey = redisTemplate.hasKey("user::001");
        log.info("user::001 hasKey:{}", hasKey);

        redisCacheUserService.selectUserByIdWithCondition("001", 21);

        hasKey = redisTemplate.hasKey("user::001");
        log.info("user::001 hasKey:{}", hasKey);

        redisCacheUserService.selectUserByIdWithCondition("001", 20);

        hasKey = redisTemplate.hasKey("user::001");
        log.info("user::001 hasKey:{}", hasKey);

        log.info("=========case 2============");
        redisCacheUserService.deleteUserById("001");
        redisCacheUserService.insertUserWithCondition(user1);

        hasKey = redisTemplate.hasKey("user::001");
        log.info("user::001 hasKey:{}", hasKey);

        redisCacheUserService.selectUserByIdWithUnless("001", 19);

        hasKey = redisTemplate.hasKey("user::001");
        log.info("user::001 hasKey:{}", hasKey);

        redisCacheUserService.selectUserByIdWithUnless("001", 20);

        hasKey = redisTemplate.hasKey("user::001");
        log.info("user::001 hasKey:{}", hasKey);

        redisCacheUserService.updateUser(user1);

        hasKey = redisTemplate.hasKey("user::001");
        log.info("user::001 hasKey:{}", hasKey);

        redisCacheUserService.deleteUserByIdAndCleanCache("001");

        hasKey = redisTemplate.hasKey("user::001");
        log.info("user::001 hasKey:{}", hasKey);
    }


    @Resource
    private RedissonClient redissonClient;
    private AtomicBoolean flag = new AtomicBoolean(false);
    /**
     * 实现两个线程交替打印 50
     */
    @Test
    void redissionLock() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        RLock lock = redissonClient.getLock("redissionLock::lock");
        // RLock lock = redissonClient.getFairLock("redissionLock::lock");

        // Condition condition1 = lock.newCondition();
        // Condition condition2 = lock.newCondition();

        /*
        RFuture<Void> voidRFuture = lock.lockAsync();

        voidRFuture
                .whenComplete((res, exception) -> {
            lock.unlockAsync();
        });
        */

        executorService.execute(() -> {
            for (int i = 0; i < 50; i++) {
                try {
                    // lock.lock();
                    boolean b = lock.tryLock(1, TimeUnit.SECONDS);
                    if (b) {
                        System.out.println(Thread.currentThread().getName() + "--" + i);
                    } else {
                        System.out.println(Thread.currentThread().getName() + "--获取锁失败");
                    }
                } catch (InterruptedException e) {
                    log.error("InterruptedException", e);
                } finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    // if (lock.isLocked()) {
                        lock.unlock();
                    }
                }
            }
        });
        executorService.execute(() -> {
            for (int i = 0; i < 50; i++) {
                try {
                    // lock.lock();
                    boolean b = lock.tryLock(1, TimeUnit.SECONDS);
                    if (b) {
                        System.out.println(Thread.currentThread().getName() + "--" + i);
                    } else {
                        System.out.println(Thread.currentThread().getName() + "--获取锁失败");
                    }
                } catch (InterruptedException e) {
                    log.error("InterruptedException", e);
                } finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    // if (lock.isLocked()) {
                        lock.unlock();
                    }
                }
            }
        });


        TimeUnit.MINUTES.sleep(3);
    }

    /**
     * 实现两个线程交替打印 31, 步长为3
     */
    @Test
    void redissionConditionLock() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        ReentrantLock lock = new ReentrantLock();
        Condition conditionA = lock.newCondition();
        Condition conditionB = lock.newCondition();

        //NumberWrapper只是为了封装一个数字，一边可以将数字对象共享，并可以设置为final
        //注意这里不要用Integer, Integer 是不可变对象
        final AtomicInteger num = new AtomicInteger(0);

        executorService.execute(() -> {
            for (int i = 1; i < 31; i++) {
                try {
                    lock.lock();
                    while (num.get() == 0) {
                        conditionA.await();
                    }
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(Thread.currentThread().getName() + "进入了awaitA方法" + i);
                    if (i % 3 == 0) {
                        num.set(0);
                        conditionB.signal();
                    }
                } catch (InterruptedException e) {
                    log.error("e", e);
                } finally {
                    lock.unlock();
                }
            }
        });
        executorService.execute(() -> {
            for (int i = 1; i < 31; i++) {
                try {
                    lock.lock();
                    while (num.get() == 1) {
                        conditionB.await();
                    }
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(Thread.currentThread().getName() + "进入了awaitB方法" + i);
                    if (i % 3 == 0) {
                        num.set(1);
                        conditionA.signal();
                    }
                } catch (InterruptedException e) {
                    log.error("e", e);
                } finally {
                    lock.unlock();
                }
            }
        });


        TimeUnit.MINUTES.sleep(3);
    }
}
