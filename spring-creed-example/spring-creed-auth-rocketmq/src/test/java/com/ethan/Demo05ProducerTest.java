package com.ethan;

import com.ethan.producer.Demo04Producer;
import com.ethan.producer.Demo05Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

/**
 * 消息失败重试
 */
//@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
public class Demo05ProducerTest {
  @Autowired
  private Demo05Producer producer;

  @Test
  public void test() throws InterruptedException {
    // 阻塞等待，保证消费
    new CountDownLatch(1).await();
  }

  @Test
  public void testSyncSend() throws InterruptedException {
    int id = (int) (System.currentTimeMillis() / 1000);
    SendResult result = producer.syncSend(id);
    log.info("[testSyncSend][发送编号：[{}] 发送结果：[{}]]", id, result);

    // 阻塞等待，保证消费
    new CountDownLatch(1).await();
  }

}
