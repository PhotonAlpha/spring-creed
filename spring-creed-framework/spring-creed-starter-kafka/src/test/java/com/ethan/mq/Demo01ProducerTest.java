/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq;

import com.ethan.mq.producer.Demo01Producer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

@Slf4j
@SpringBootTest(classes = MqApplication.class)
public class Demo01ProducerTest {
    @Autowired
    private Demo01Producer producer;
    @Test
    public void testSyncSend() throws ExecutionException, InterruptedException {
        int id = (int) (System.currentTimeMillis() / 1000);
        SendResult result = producer.syncSend(id);
        log.info("[testSyncSend][发送编号：[{}] 发送结果：[{}]]", id, result);
        // 阻塞等待，保证消费
        new CountDownLatch(1).await();
    }
    @Test
    public void testASyncSend() throws InterruptedException {
        int id = (int) (System.currentTimeMillis() / 1000);
        /* @Override
        public void onFailure(Throwable e) {
            log.info("[testASyncSend][发送编号：[{}] 发送异常]]", id, e);
        }
        @Override
        public void onSuccess(SendResult<Object, Object> result) {
            log.info("[testASyncSend][发送编号：[{}] 发送成功，结果为：[{}]]", id, result);
        } */
        producer.asyncSend(id).whenComplete((BiConsumer<Object, Object>) (o, o2) -> log.info("[testASyncSend][发送编号：[{}] 发送异常:{}]]", o, o2));
        // 阻塞等待，保证消费
        new CountDownLatch(1).await();
    }
}
