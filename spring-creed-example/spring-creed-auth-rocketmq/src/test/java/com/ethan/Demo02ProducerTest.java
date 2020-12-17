package com.ethan;

import com.ethan.producer.Demo01Producer;
import com.ethan.producer.Demo02Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.junit.jupiter.api.Test;
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
@Slf4j
public class Demo02ProducerTest {
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
