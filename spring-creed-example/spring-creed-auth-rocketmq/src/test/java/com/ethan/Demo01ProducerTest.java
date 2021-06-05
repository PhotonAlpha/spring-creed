package com.ethan;

import com.ethan.producer.Demo01Producer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

/**
 * If this is new application then we shall be writing new unit tests otherwise we shall be making code changes to migrate from Junit 4 to Junit 5.
 * 2.1. Remove @RunWith(SpringRunner.class)
 *
 * With Junit 5, we do not need @RunWith(SpringRunner.class) anymore. Spring tests are executed with @ExtendWith(SpringExtension.class) and @SpringBootTest and the other @…Test annotations are already annotated with it.
 */
//@ExtendWith(SpringExtension.class)
@SpringBootTest
public class Demo01ProducerTest {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Demo01ProducerTest.class);
	@Autowired
  private Demo01Producer producer;

  @Test
  public void testSyncSend() throws InterruptedException {
    int id = (int) (System.currentTimeMillis() / 1000);
    SendResult result = producer.syncSend(id);
    log.info("[testSyncSend][发送编号：[{}] 发送结果：[{}]]", id, result);

    // 阻塞等待，保证消费
    new CountDownLatch(1).await();
    //com.ethan.Demo01ProducerTest             : [testSyncSend][发送编号：[1584947662] 发送结果：[SendResult [sendStatus=SEND_OK, msgId=0000000000000000000000000000000100002626B41874910B5B0000, offsetMsgId=0AC008CB00002A9F0000000000001972, messageQueue=MessageQueue [topic=DEMO_01, brokerName=broker-a, queueId=1], queueOffset=2]]]
    //com.ethan.consumer.Demo01AConsumer       : [onMessage][线程编号:58 消息内容：MessageExt [queueId=1, storeSize=299, queueOffset=2, sysFlag=0, bornTimestamp=1584947662798, bornHost=/10.192.8.203:55275, storeTimestamp=1584947662813, storeHost=/10.192.8.203:10911, msgId=0AC008CB00002A9F0000000000001972, commitLogOffset=6514, bodyCRC=1886143297, reconsumeTimes=0, preparedTransactionOffset=0, toString()=Message{topic='DEMO_01', flag=0, properties={MIN_OFFSET=0, MAX_OFFSET=3, CONSUME_START_TIME=1584947662833, id=d4f5c23e-43fd-f12b-c309-ec21ac9b8b49, UNIQ_KEY=0000000000000000000000000000000100002626B41874910B5B0000, WAIT=false, contentType=application/json;charset=UTF-8, timestamp=1584947662567}, body=[123, 34, 105, 100, 34, 58, 49, 53, 56, 52, 57, 52, 55, 54, 54, 50, 125], transactionId='null'}]]
    //com.ethan.consumer.Demo01Consumer        : [onMessage][线程编号:57 消息内容：Demo01Message(id=1584947662)]

  }

  @Test
  public void testASyncSend() throws InterruptedException {
    int id = (int) (System.currentTimeMillis() / 1000);
    producer.asyncSend(id, new SendCallback() {

      @Override
      public void onSuccess(SendResult result) {
        log.info("[testASyncSend][发送编号：[{}] 发送成功，结果为：[{}]]", id, result);
      }

      @Override
      public void onException(Throwable e) {
        log.info("[testASyncSend][发送编号：[{}] 发送异常]]", id, e);
      }

    });

    // 阻塞等待，保证消费
    new CountDownLatch(1).await();
  }

  @Test
  public void testOnewaySend() throws InterruptedException {
    int id = (int) (System.currentTimeMillis() / 1000);
    producer.onewaySend(id);
    log.info("[testOnewaySend][发送编号：[{}] 发送完成]", id);

    // 阻塞等待，保证消费
    new CountDownLatch(1).await();
  }

}
