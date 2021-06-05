package com.ethan.producer;

import com.ethan.message.Demo05Message;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Demo05Producer {
  @Autowired
  RocketMQTemplate mqTemplate;

  public SendResult syncSend(Integer id) {
    // 创建 Demo05Message 消息
    Demo05Message message = new Demo05Message().setId(id);
    // 同步发送消息
    return mqTemplate.syncSend(Demo05Message.TOPIC, message);
  }
}
