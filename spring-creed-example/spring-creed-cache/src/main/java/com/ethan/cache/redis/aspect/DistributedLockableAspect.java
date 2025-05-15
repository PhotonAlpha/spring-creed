package com.ethan.cache.redis.aspect;

import com.ethan.cache.redis.annotation.DistributedLockable;
import com.ethan.cache.redis.lock.RedisLockClient;
import com.ethan.cache.redis.support.KeyGenerator;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;


@Aspect
@Order(10)
@Deprecated
public class DistributedLockableAspect implements KeyGenerator {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  @Resource
  private RedisLockClient redisLockClient;

  /**
   * {@link DistributedLockable}
   * @author piaoruiqing
   *
   */
  @Pointcut(value = "execution(* *(..)) && @annotation(com.ethan.cache.redis.annotation.DistributedLockable)")
  public void distributedLockable() {}

  /**
   * @author piaoruiqing
   *
   * @param joinPoint
   * @param lockable
   * @return
   * @throws Throwable
   */
  @Around(value = "distributedLockable() && @annotation(lockable)")
  public Object around(ProceedingJoinPoint joinPoint, DistributedLockable lockable) throws Throwable {

    long start = System.nanoTime();
    final String key = this.generate(joinPoint, lockable.prefix(), lockable.argNames(), lockable.argsAssociated()).toString();

    Object result = redisLockClient.tryLock(
        key, () -> {
          return joinPoint.proceed();
        },
        lockable.unit().toMillis(lockable.timeout()), lockable.autoUnlock(),
        lockable.retries(), lockable.unit().toMillis(lockable.waitingTime()),
        lockable.onFailure()
    );

    long end = System.nanoTime();
    LOGGER.info("distributed lockable cost: {} ns", end - start);

    return result;
  }
}
