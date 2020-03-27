package com.ethan.producer;

import com.ethan.message.Demo01Message;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Demo01Producer {
  @Autowired
  RocketMQTemplate mqTemplate;

  public SendResult syncSend(Integer id) {
    // 创建 Demo01Message 消息
    Demo01Message message = new Demo01Message();
    message.setId(id);
    // 同步发送消息
    return mqTemplate.syncSend(Demo01Message.TOPIC, message);
  }

  public void asyncSend(Integer id, SendCallback callback) {
    // 创建 Demo01Message 消息
    Demo01Message message = new Demo01Message();
    message.setId(id);
    // 异步发送消息
    mqTemplate.asyncSend(Demo01Message.TOPIC, message, callback);
  }

  public void onewaySend(Integer id) {
    // 创建 Demo01Message 消息
    Demo01Message message = new Demo01Message();
    message.setId(id);
    // oneway 发送消息
    mqTemplate.sendOneWay(Demo01Message.TOPIC, message);
  }
}
