package com.ethan.consumer;

import com.ethan.message.Demo02Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;

//@Component
@RocketMQMessageListener(
    topic = Demo02Message.TOPIC,
    consumerGroup = "demo02-consumer-group-" + Demo02Message.TOPIC
)
public class Demo02Consumer implements RocketMQListener<Demo02Message> {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Demo02Consumer.class);

	@Override
  public void onMessage(Demo02Message message) {
    log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
  }
}
