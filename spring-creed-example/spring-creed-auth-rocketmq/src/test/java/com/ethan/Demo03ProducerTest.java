package com.ethan;

import com.ethan.producer.Demo03Producer;
import org.apache.rocketmq.client.producer.SendResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

/**
 * 定时消息
 */
//@ExtendWith(SpringExtension.class)
@SpringBootTest
public class Demo03ProducerTest {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Demo03ProducerTest.class);
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
