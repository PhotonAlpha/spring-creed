package com.ethan;

import com.ethan.producer.Demo05Producer;
import com.ethan.producer.Demo07Producer;
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
public class Demo07ProducerTest {
  @Autowired
  private Demo07Producer producer;

  @Test
  public void testSendMessageInTransaction() throws InterruptedException {
    int id = (int) (System.currentTimeMillis() / 1000);
    SendResult result = producer.sendMessageInTransaction(id);
    log.info("[testSendMessageInTransaction][发送编号：[{}] 发送结果：[{}]]", id, result);
    //SendResult result2 = producer.sendMessageInTransaction(123);
    //log.info("[testSendMessageInTransaction][发送编号：[{}] 发送结果：[{}]]", id, result2);

    // 阻塞等待，保证消费
    new CountDownLatch(1).await();
  }

}
