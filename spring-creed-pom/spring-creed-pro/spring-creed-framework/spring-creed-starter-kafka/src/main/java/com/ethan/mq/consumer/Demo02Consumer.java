/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq.consumer;

import com.ethan.mq.message.Demo02Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class Demo02Consumer {
    /**
     * 监听单个消息
     * @param message
     */
    /* @KafkaListener(topics = Demo02Message.TOPIC,
            groupId = "demo02-consumer-group-" + Demo02Message.TOPIC)
    public void onMessage(Demo02Message message) {
        log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
    } */

    /**
     *    kafka.listener.type: batch # 监听器类型，默认为 SINGLE ，只监听单条消息。这里我们配置 BATCH ，监听多条消息，批量消费
     * @param message
     */
    @KafkaListener(topics = Demo02Message.TOPIC,
            groupId = "demo02-consumer-group-" + Demo02Message.TOPIC)
    public void onMessage(List<Demo02Message> message) {
        log.info("[onMessage][线程编号:{} 消息数量：{}]", Thread.currentThread().getId(), message.size());
        for (Demo02Message demo02Message : message) {
            log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), demo02Message);
        }
    }
}
