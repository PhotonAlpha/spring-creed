/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq;

import com.ethan.mq.producer.Demo04Producer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import org.junit.jupiter.api.Disabled;

@Slf4j
@SpringBootTest(classes = MqApplication.class)
@Disabled("手动演示用例：依赖本地 Kafka，Spring 上下文无法启动，不适合 CI 执行")
public class Demo04ProducerTest {
    @Autowired
    private Demo04Producer producer;

    @Test
    public void testASyncSend() throws InterruptedException {
        int id = (int) (System.currentTimeMillis() / 1000);
        producer.asyncSend(id).whenComplete((BiConsumer<Object, Object>) (o, o2) -> log.info("[testASyncSend][发送编号：[{}] 发送异常:{}]]", o, o2));
        // 阻塞等待，保证消费
        new CountDownLatch(1).await();
    }
}
