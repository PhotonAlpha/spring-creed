package com.ethan.consumer;

import com.ethan.message.Demo01Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
    topic = Demo01Message.TOPIC,
    consumerGroup = "demo01-consumer-group-" + Demo01Message.TOPIC
)
public class Demo01Consumer implements RocketMQListener<Demo01Message> {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Demo01Consumer.class);

	@Override
  public void onMessage(Demo01Message message) {
    log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
  }
}
