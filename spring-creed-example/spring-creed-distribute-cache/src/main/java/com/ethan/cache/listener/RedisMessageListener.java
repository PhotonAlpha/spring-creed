package com.ethan.cache.listener;

import com.ethan.cache.LayeringCache;
import com.ethan.cache.constants.CacheConstant;
import com.ethan.cache.constants.ChannelTopicEnum;
import com.ethan.common.utils.json.JacksonUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * redis message subscribe channel
 */
@Component
public class RedisMessageListener extends MessageListenerAdapter {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(RedisMessageListener.class);
	@Autowired
  private CacheManager cacheManager;


  @Override
  public void onMessage(Message message, byte[] pattern) {
    super.onMessage(message, pattern);
    ChannelTopicEnum channelTopic = ChannelTopicEnum.getChannelTopicEnum(new String(message.getChannel()));
    log.info("redis publisher to channel {} and published {}", channelTopic.getChannelTopicStr(), message.toString().getBytes());
    // Parse the information published by the subscription, get the cache name and cache key
    String ms = new String(message.getBody());
    Map<String, Object> map = JacksonUtils.parseObject(ms, HashMap.class);
    String cacheName = (String) map.get(CacheConstant.CACHE_NAME);
    Object key = map.get(CacheConstant.CACHE_KEY);

    // get multi-level cache base on the cache name
    Cache cache = cacheManager.getCache(cacheName);
    if (cache != null && cache instanceof LayeringCache) {
      switch (channelTopic) {
        case REDIS_CACHE_CLEAR_TOPIC:
          ((LayeringCache) cache).getPrimaryCache().clear();
          log.info("cleaning L1 cache{} ,key:{}", cacheName, key);
          break;
        case REDIS_CACHE_DELETE_TOPIC:
          ((LayeringCache) cache).getPrimaryCache().evict(key);
          log.info("deleting L1 cache{} ,key:{}", cacheName, key);
          break;
        default:
          logger.info("unknown channel message received.");
          break;
      }
    }

  }
}
