package com.ethan.consumer;

import com.ethan.message.Demo04Message;
import com.ethan.message.Demo05Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
    topic = Demo05Message.TOPIC,
    consumerGroup = "demo05-consumer-group-" + Demo05Message.TOPIC,
    messageModel = MessageModel.BROADCASTING
)
public class Demo05Consumer implements RocketMQListener<Demo05Message> {
  @Override
  public void onMessage(Demo05Message message) {
    log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
  }
}
