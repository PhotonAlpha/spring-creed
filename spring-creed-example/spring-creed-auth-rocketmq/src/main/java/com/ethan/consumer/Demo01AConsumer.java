package com.ethan.consumer;

import com.ethan.message.Demo01Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
    topic = Demo01Message.TOPIC,
    consumerGroup = "demo01-A-consumer-group-" + Demo01Message.TOPIC
)
public class Demo01AConsumer implements RocketMQListener<MessageExt> {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Demo01AConsumer.class);

	@Override
  public void onMessage(MessageExt message) {
    log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
  }
}
