/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq.producer;

import com.ethan.mq.message.Demo04Message;
import jakarta.annotation.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class Demo04Producer {
    @Resource
    private KafkaTemplate<Object, Object> kafkaTemplate;
    public CompletableFuture<SendResult<Object, Object>> asyncSend(Integer id) {
        // 创建 Demo04Message 消息
        Demo04Message message = new Demo04Message();
        message.setId(id);
        // 异步发送消息
        return kafkaTemplate.send(Demo04Message.TOPIC, message);
    }


}
