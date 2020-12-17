package com.ethan;

import com.ethan.producer.Demo02Producer;
import com.ethan.producer.Demo03Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 定时消息
 */
//@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
public class Demo03ProducerTest {
  @Autowired
  private Demo03Producer producer;

  @Test
  public void testSyncSendDelay() throws InterruptedException {
    int id = (int) (System.currentTimeMillis() / 1000);
    SendResult result = producer.syncSendDelay(id, 3); // 延迟级别 3 ，即 10 秒后消费
    log.info("[testSyncSendDelay][发送编号：[{}] 发送结果：[{}]]", id, result);
    result = producer.syncSendDelay(id, 3); // 延迟级别 3 ，即 10 秒后消费
    log.info("[testSyncSendDelay][发送编号：[{}] 发送结果：[{}]]", id, result);

    // 阻塞等待，保证消费
    new CountDownLatch(1).await();
  }

}
