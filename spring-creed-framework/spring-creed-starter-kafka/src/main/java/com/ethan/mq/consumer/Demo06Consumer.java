/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq.consumer;

import com.ethan.mq.message.Demo06Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Demo06Consumer {


    /**
     * kafka.listener.type: batch # 监听器类型，默认为 SINGLE ，只监听单条消息。这里我们配置 BATCH ，监听多条消息，批量消费
     *
     * @param message
     */
    @KafkaListener(topics = Demo06Message.TOPIC,
            groupId = "demo06-consumer-group-" + Demo06Message.TOPIC,
            concurrency = "3")
    public void onMessage(Demo06Message message) {
        log.info("[onMessage][线程编号:{} 消息数量：{}]", Thread.currentThread().getId(), message);
    }
}
