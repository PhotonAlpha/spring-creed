/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq.consumer;

import com.ethan.mq.message.Demo08Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Demo08Consumer {


    @KafkaListener(topics = Demo08Message.TOPIC,
            groupId = "demo08-consumer-group-" + Demo08Message.TOPIC)
    public void onMessage(Demo08Message message, Acknowledgment acknowledgment) {
        log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
        // 提交消费进度
        // 在消费逻辑中，我们故意只提交消费的消息的 Demo08Message.id 为奇数的消息。
        // 这样，我们只需要发送一条 id=1 ，一条 id=2 的消息，如果第二条的消费进度没有被提交，就可以说明手动提交消费进度成功。
        if (message.getId() % 2 == 1) {
            acknowledgment.acknowledge();
        }
    }
}
