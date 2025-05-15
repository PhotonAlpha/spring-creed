/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2019/12/10
 */
package com.ethan.cache.redis;

import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * the thread await container
 */
public class ThreadAwaitContainer {
  private final Map<String, Set<Thread>> threadMap = new ConcurrentHashMap<>();

  /**
   * thread await max time, default is 100ms
   * @param key cache key
   * @param milliseconds wait time
   */
  public final void await(String key, long milliseconds) throws InterruptedException {
    if (Thread.interrupted()) {
      // if current thread interrupted, throw the exception
      throw new InterruptedException();
    }
    Set<Thread> threadSet = threadMap.get(key);
    if (threadSet == null) {
      threadSet = new HashSet<>();
      threadMap.put(key, threadSet);
    }
    // set the current thread
    threadSet.add(Thread.currentThread());
    // lock the current thread
    LockSupport.parkNanos(this, TimeUnit.MILLISECONDS.toNanos(milliseconds));
  }

  /**
   * wake up the thread
   * @param key
   */
  public final void signalAll(String key) {
    Set<Thread> threadSet = threadMap.get(key);
    // check the await container is null which correspond the key
    if (! CollectionUtils.isEmpty(threadSet)) {
      for (Thread thread : threadSet) {
        LockSupport.unpark(thread);
      }
      // clean the container
      threadSet.clear();
    }
  }
}
