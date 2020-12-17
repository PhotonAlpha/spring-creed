package com.ethan.interceptor;

import com.ethan.annotation.CacheKeyGenerator;
import com.ethan.annotation.CacheLock;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.lang.reflect.Method;

@Configuration
@Aspect
public class CacheLockInterceptor {
  private final StringRedisTemplate lockRedisTemplate;
  private final CacheKeyGenerator cacheKeyGenerator = CacheKeyGenerator.simple();

  public CacheLockInterceptor(StringRedisTemplate lockRedisTemplate) {
    this.lockRedisTemplate = lockRedisTemplate;
  }

  @Around("execution(public * * (..)) && @annotation(com.ethan.annotation.CacheLock)")
  public Object interceptor(ProceedingJoinPoint pjp) {
    MethodSignature signature = (MethodSignature) pjp.getSignature();
    Method method = signature.getMethod();
    CacheLock lock = method.getAnnotation(CacheLock.class);
    if (StringUtils.isEmpty(lock.prefix())) {
      throw new RuntimeException("lock key don't null...");
    }
    final String lockKey = cacheKeyGenerator.getLockKey(pjp);
    try {
      // 采用原生 API 来实现分布式锁
      final Boolean success = lockRedisTemplate.execute((RedisCallback<Boolean>) connection ->
          connection.set(lockKey.getBytes(), new byte[0], Expiration.from(lock.expire(), lock.timeUnit()), RedisStringCommands.SetOption.SET_IF_ABSENT));
      if (!success) {
        // TODO 按理来说 我们应该抛出一个自定义的 CacheLockException 异常;这里偷下懒
        throw new RuntimeException("请勿重复请求");
      }
      try {
        return pjp.proceed();
      } catch (Throwable throwable) {
        throw new RuntimeException("系统异常");
      }
    } finally {
      // TODO 如果演示的话需要注释该代码;实际应该放开
      // lockRedisTemplate.delete(lockKey);
    }
  }
}
