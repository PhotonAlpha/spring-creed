/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq;

import com.ethan.mq.producer.Demo06Producer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

@Slf4j
@SpringBootTest(classes = MqApplication.class)
public class Demo06ProducerTest {
    @Autowired
    private Demo06Producer producer;

    @Test
    public void testASyncSend() throws InterruptedException, ExecutionException {
        log.info("[testASyncSend][开始执行]");
        for (int i = 0; i < 10; i++) {
            int id = (int) (System.currentTimeMillis() / 1000);
            log.info("[testASyncSend][开始执行]:" + i);
            SendResult<Object, Object> result = producer.syncSendOrderly(i);
            log.info("[testSyncSend][发送编号：[{}] 发送队列：[{}]]", id, result.getRecordMetadata().partition());
        }
        // 阻塞等待，保证消费
        new CountDownLatch(1).await();
    }
}
