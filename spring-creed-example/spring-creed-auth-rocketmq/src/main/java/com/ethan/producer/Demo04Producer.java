package com.ethan.producer;

import com.ethan.message.Demo03Message;
import com.ethan.message.Demo04Message;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class Demo04Producer {
  @Autowired
  RocketMQTemplate mqTemplate;

  public SendResult syncSend(Integer id) {
    // 创建 Demo04Message 消息
    Demo04Message message = new Demo04Message();
    message.setId(id);
    // 同步发送消息
    return mqTemplate.syncSend(Demo04Message.TOPIC, message);
  }
}
