/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq;

import com.ethan.mq.producer.Demo02Producer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;

@Slf4j
@SpringBootTest(classes = MqApplication.class)
public class Demo02ProducerTest {
    @Autowired
    private Demo02Producer producer;

    @Test
    public void testASyncSend() throws InterruptedException {
        log.info("[testASyncSend][开始执行]");
        for (int i = 0; i < 3; i++) {
            int id = (int) (System.currentTimeMillis() / 1000);
            log.info("[testASyncSend][开始执行]:" + i);
            producer.asyncSend(id).whenComplete((BiConsumer<Object, Object>) (o, o2) -> log.info("[testASyncSend][发送编号：[{}] 发送异常:{}]]", o, o2));
            // 故意每条消息之间，隔离 10 秒
            Thread.sleep(10 * 1000L);
        }
        // 阻塞等待，保证消费
        new CountDownLatch(1).await();
    }
}
