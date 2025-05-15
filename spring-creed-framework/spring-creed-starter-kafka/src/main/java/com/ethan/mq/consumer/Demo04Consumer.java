/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq.consumer;

import com.ethan.mq.message.Demo04Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Demo04Consumer {


    /**
     *    kafka.listener.type: batch # 监听器类型，默认为 SINGLE ，只监听单条消息。这里我们配置 BATCH ，监听多条消息，批量消费
     * @param message
     */
    @KafkaListener(topics = Demo04Message.TOPIC,
            groupId = "demo04-consumer-group-" + Demo04Message.TOPIC)
    public void onMessage(Demo04Message message) {
        log.info("[onMessage][线程编号:{} 消息数量：{}]", Thread.currentThread().getId(), message);
        throw new RuntimeException("我就是故意抛出一个异常");
    }
}
