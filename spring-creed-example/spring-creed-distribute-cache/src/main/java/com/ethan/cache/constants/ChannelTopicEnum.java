package com.ethan.cache.constants;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.stream.Stream;

public enum ChannelTopicEnum {
  REDIS_CACHE_DELETE_TOPIC("redis:cache:delete:topic", "deleting redis cache channel"),
  REDIS_CACHE_CLEAR_TOPIC("redis:cache:clear:topic", "cleaning redis cache channel");
  String channelTopic;
  String label;

  ChannelTopicEnum(String channelTopic, String label) {
    this.channelTopic = channelTopic;
    this.label = label;
  }

  public ChannelTopic getChannelTopic() {
    return new ChannelTopic(this.channelTopic);
  }

  public static ChannelTopicEnum getChannelTopicEnum(String channelTopic) {
    return Stream.of(ChannelTopicEnum.values())
        .filter(e -> StringUtils.equalsIgnoreCase(e.getChannelTopicStr(), channelTopic))
        .findFirst().orElse(null);
  }

  public String getChannelTopicStr() {
    return channelTopic;
  }
  public String getLabel() {
    return label;
  }

}
