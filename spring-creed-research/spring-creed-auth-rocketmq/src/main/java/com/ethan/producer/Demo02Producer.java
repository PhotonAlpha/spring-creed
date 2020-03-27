package com.ethan.producer;

import com.ethan.message.Demo01Message;
import com.ethan.message.Demo02Message;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class Demo02Producer {
  @Autowired
  RocketMQTemplate mqTemplate;

  public SendResult sendBatch(Collection<Integer> ids) {
    // <X> 创建多条 Demo02Message 消息
    List<Message> messages = new ArrayList<>(ids.size());
    for (Integer id : ids) {
      // 创建 Demo02Message 消息
      Demo02Message message = new Demo02Message();
      message.setId(id);
      // 构建 Spring Messaging 定义的 Message 消息
      messages.add(MessageBuilder.withPayload(message).build());
    }
    // 同步批量发送消息
    return mqTemplate.syncSend(Demo02Message.TOPIC, messages, 30 * 1000L);
  }
}
