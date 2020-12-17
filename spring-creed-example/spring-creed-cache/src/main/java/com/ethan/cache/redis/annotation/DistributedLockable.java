package com.ethan.cache.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedLockable {
  /**
   * timeout of the lock
   *
   * @author piaoruiqing
   *
   * @return
   */
  long timeout() default 5L;

  /**
   * time unit
   * @author piaoruiqing
   *
   * @return
   */
  TimeUnit unit() default TimeUnit.MILLISECONDS;

  /**
   * number of retries
   *
   * @author piaoruiqing
   *
   * @return
   */
  int retries() default 0;

  /**
   * interval of each retry
   *
   * @author piaoruiqing
   *
   * @return
   */
  long waitingTime() default 0L;

  /**
   * key prefix
   *
   * @author piaoruiqing
   *
   * @return
   */
  String prefix() default "";

  /**
   * parameters that construct a key
   *
   * @author piaoruiqing
   *
   * @return
   */
  String[] argNames() default {};

  /**
   * construct a key with parameters
   *
   * @author piaoruiqing
   *
   * @return
   */
  boolean argsAssociated() default true;

  /**
   * whether unlock when completed
   *
   * @author piaoruiqing
   *
   * @return
   */
  boolean autoUnlock() default true;

  /**
   * throw an runtime exception while fail to get lock
   *
   * @author piaoruiqing
   *
   * @return
   */
  Class<? extends RuntimeException> onFailure() default NoException.class;

  /**
   * no exception
   *
   * @author piaoruiqing
   * @date: 2019/05/18 09:31
   *
   * @since JDK 1.8
   */
  public static final class NoException extends RuntimeException {

    private static final long serialVersionUID = -7821936618527445658L;

  }
}
