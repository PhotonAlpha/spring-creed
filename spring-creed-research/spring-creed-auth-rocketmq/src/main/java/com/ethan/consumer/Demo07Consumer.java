package com.ethan.consumer;

import com.ethan.message.Demo07Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
    topic = Demo07Message.TOPIC,
    consumerGroup = "demo07-consumer-group-" + Demo07Message.TOPIC
)
public class Demo07Consumer implements RocketMQListener<Demo07Message> {
  @Override
  public void onMessage(Demo07Message message) {
    log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
  }
}
