package com.ethan.cache.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;

@Slf4j
public class RedisPublisher {
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
