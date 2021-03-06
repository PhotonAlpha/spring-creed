package com.ethan.consumer;

import com.ethan.message.Demo04Message;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;

//@Component
@RocketMQMessageListener(
    topic = Demo04Message.TOPIC,
    consumerGroup = "demo04-consumer-group-" + Demo04Message.TOPIC
)
public class Demo04Consumer implements RocketMQListener<Demo04Message> {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Demo04Consumer.class);

	@Override
  public void onMessage(Demo04Message message) {
    log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
    // <X> 注意，此处抛出一个 RuntimeException 异常，模拟消费失败
    throw new RuntimeException("我就是故意抛出一个异常");
  }
}
