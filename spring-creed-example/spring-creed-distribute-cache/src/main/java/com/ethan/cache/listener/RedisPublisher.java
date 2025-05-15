package com.ethan.cache.listener;

import org.slf4j.Logger;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;

public class RedisPublisher {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(RedisPublisher.class);
	RedisOperations<? extends Object, ? extends Object> redisOperations;
  ChannelTopic channelTopic;

  public RedisPublisher(RedisOperations<? extends Object, ? extends Object> redisOperations, ChannelTopic channelTopic) {
    this.redisOperations = redisOperations;
    this.channelTopic = channelTopic;
  }

  /**
   * publish message to channel
   * @param message
   */
  public void publisher(Object message) {
    redisOperations.convertAndSend(channelTopic.toString(), message);
    log.info("redis publisher to channel {} and published {}", channelTopic.toString(), message.toString());
  }
}
