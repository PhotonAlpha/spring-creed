/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq;

import com.ethan.mq.producer.Demo08Producer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

@Slf4j
@SpringBootTest(classes = MqApplication.class)
@ActiveProfiles("demo08")
public class Demo08ProducerTest {
    @Autowired
    private Demo08Producer producer;

    @Test
    public void testSyncSend() throws ExecutionException, InterruptedException {
        for (int id = 1; id <= 2; id++) {
            SendResult result = producer.syncSend(id);
            log.info("[testSyncSend][发送编号：[{}] 发送结果：[{}]]", id, result);
        }
        // 阻塞等待，保证消费
        new CountDownLatch(1).await();
    }
}
