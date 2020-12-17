package com.ethan.cache.redis.lock;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.List;

/**
 * this is an annotation implement
 */
@Deprecated
public class RedisDistributedLock extends DistributedLock {
  private RedisOperations<String, String> operations;
  private String key;
  private String value;
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

  public RedisDistributedLock(RedisOperations<String, String> operations, String key, String value) {
    this.operations = operations;
    this.key = key;
    this.value = value;
  }

  @Override
  public void release() {
    List<String> keys = Collections.singletonList(key);
    operations.execute(new DefaultRedisScript<Object>(UNLOCK_LUA, Object.class), keys, value);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "RedisDistributedLock [key=" + key + ", value=" + value + "]";
  }
}
