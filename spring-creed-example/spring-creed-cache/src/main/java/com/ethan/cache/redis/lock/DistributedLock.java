package com.ethan.cache.redis.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public abstract class DistributedLock implements AutoCloseable {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());
  /**
   * release lock
   * @author piaoruiqing
   */
  abstract public void release();

  @Override
  public void close() throws Exception {
    LOGGER.info("distributed lock close , {}", this.toString());

    this.release();
  }
}
