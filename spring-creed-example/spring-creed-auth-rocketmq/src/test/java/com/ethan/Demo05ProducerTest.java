package com.ethan;

import com.ethan.producer.Demo05Producer;
import org.apache.rocketmq.client.producer.SendResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

/**
 * 消息失败重试
 */
//@ExtendWith(SpringExtension.class)
@SpringBootTest
public class Demo05ProducerTest {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Demo05ProducerTest.class);
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
