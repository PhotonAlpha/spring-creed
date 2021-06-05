package com.ethan.consumer;

import com.ethan.message.Demo03Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;

//@Component
@RocketMQMessageListener(
    topic = Demo03Message.TOPIC,
    consumerGroup = "demo03-consumer-group-" + Demo03Message.TOPIC
)
public class Demo03Consumer implements RocketMQListener<Demo03Message> {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Demo03Consumer.class);

	@Override
  public void onMessage(Demo03Message message) {
    log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
  }
}
