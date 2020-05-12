package com.ethan.gradation.listener;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

/**
 * redis消息的发布者
 *
 */
@Slf4j
public class RedisPublisher {

    private RedisPublisher() {
    }

    /**
     * 发布消息到频道（Channel）
     *
     * @param redisTemplate redis客户端
     * @param channelTopic  发布预订阅的频道
     * @param message       消息内容
     */
    public static void publisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic channelTopic, Object message) {
        redisTemplate.convertAndSend(channelTopic.toString(), message);
        log.debug("redis消息发布者向频道【{}】发布了【{}】消息", channelTopic.toString(), message.toString());
    }
}
