package com.ethan;

import com.ethan.producer.Demo02Producer;
import org.apache.rocketmq.client.producer.SendResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 批量消息
 */
//@ExtendWith(SpringExtension.class)
@SpringBootTest
public class Demo02ProducerTest {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Demo02ProducerTest.class);
	@Autowired
  private Demo02Producer producer;

  @Test
  public void testSendBatch() throws InterruptedException {
    List<Integer> ids = Arrays.asList(1, 2, 3);
    SendResult result = producer.sendBatch(ids);
    log.info("[testSendBatch][发送编号：[{}] 发送结果：[{}]]", ids, result);

    // 阻塞等待，保证消费
    new CountDownLatch(1).await();
  }

}
