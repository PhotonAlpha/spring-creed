/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq.producer;

import com.ethan.mq.message.Demo05Message;
import jakarta.annotation.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class Demo05Producer {
    @Resource
    private KafkaTemplate<Object, Object> kafkaTemplate;
    public SendResult<Object, Object> syncSend(Integer id) throws ExecutionException, InterruptedException {
        // 创建 Demo05Message 消息
        Demo05Message message = new Demo05Message();
        message.setId(id);
        // 异步发送消息
        return kafkaTemplate.send(Demo05Message.TOPIC, message).get();
    }
}
