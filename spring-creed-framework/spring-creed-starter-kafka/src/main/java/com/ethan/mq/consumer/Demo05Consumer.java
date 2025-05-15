/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq.consumer;

import com.ethan.mq.message.Demo05Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Demo05Consumer {


    /**
     * kafka.listener.type: batch # 监听器类型，默认为 SINGLE ，只监听单条消息。这里我们配置 BATCH ，监听多条消息，批量消费
     *
     * @param message
     */
    @KafkaListener(topics = Demo05Message.TOPIC,
            groupId = "demo05-consumer-group-" + Demo05Message.TOPIC + "-" + "#{T(java.util.UUID).randomUUID()}",
    properties = "auto.offset.reset:latest")
    public void onMessage(ConsumerRecord<Integer, String> record, Acknowledgment ack) {
        log.info("[onMessage][线程编号:{} 消息数量：{}]", Thread.currentThread().getId(), record);
        log.info("groupId:{}", KafkaUtils.getConsumerGroupId());
    }
}
