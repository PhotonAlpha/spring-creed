package com.ethan.interceptor;

import com.ethan.annotation.LocalLock;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 基于本地缓存
 */
@Configuration
@Aspect
public class LockMethodInterceptor {
  private static final Cache<String, Object> CACHES = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(5, TimeUnit.SECONDS)
        .build();
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(LockMethodInterceptor.class);

	@Around("execution(public * * (..)) && @annotation(com.ethan.annotation.LocalLock)")
  public Object interceptor(ProceedingJoinPoint point) {
    MethodSignature signature = (MethodSignature) point.getSignature();
    Method method = signature.getMethod();
    LocalLock localLock = method.getAnnotation(LocalLock.class);
    String key = getKey(localLock.key(), point.getArgs());
    if (!StringUtils.isEmpty(key)) {
      if (CACHES.getIfPresent(key) != null) {
        throw new RuntimeException("请勿重复请求");
      }
      // 如果是第一次请求,就将 key 当前对象压入缓存中
      CACHES.put(key, key);
    }
    try {
      return point.proceed();
    } catch (Throwable throwable) {
      throw new RuntimeException("服务器异常");
    } finally {
      // TODO 为了演示效果,这里就不调用 CACHES.invalidate(key); 代码了
    }
  }

  /**
   * key 的生成策略,如果想灵活可以写成接口与实现类的方式（TODO 后续讲解）
   *
   * @param keyExpress 表达式
   * @param args       参数
   * @return 生成的key
   */
  private String getKey(String keyExpress, Object[] args) {
    for (int i = 0; i < args.length; i++) {
      keyExpress = keyExpress.replace("arg[" + i + "]", args[i].toString());
    }
    log.info("current key:{}", keyExpress);
    return keyExpress;
  }
}
