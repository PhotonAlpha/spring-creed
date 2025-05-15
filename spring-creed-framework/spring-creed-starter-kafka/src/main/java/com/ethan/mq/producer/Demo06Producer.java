/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq.producer;

import com.ethan.mq.message.Demo06Message;
import jakarta.annotation.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class Demo06Producer {
    @Resource
    private KafkaTemplate<Object, Object> kafkaTemplate;
    public SendResult<Object, Object> asyncSend(Integer id) throws ExecutionException, InterruptedException {
        // 创建 Demo06Message 消息
        Demo06Message message = new Demo06Message();
        message.setId(id);
        // 异步发送消息
        return kafkaTemplate.send(Demo06Message.TOPIC, message).get();
    }

    // Demo06Producer.java
    public SendResult syncSendOrderly(Integer id) throws ExecutionException, InterruptedException {
        // 创建 Demo01Message 消息
        Demo06Message message = new Demo06Message();
        message.setId(id);
        // 同步发送消息
        // 因为我们使用 String 的方式序列化 key ，所以需要将 id 转换成 String
        return kafkaTemplate.send(Demo06Message.TOPIC, String.valueOf(id), message).get();
    }



}
