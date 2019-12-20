package com.ethan.cache.redis.handler;

@FunctionalInterface
public interface LockHandler<T> {
  /**
   * the logic you want to execute
   *
   * @author piaoruiqing
   *
   * @return
   * @throws Throwable
   */
  T handle() throws Throwable;
}
