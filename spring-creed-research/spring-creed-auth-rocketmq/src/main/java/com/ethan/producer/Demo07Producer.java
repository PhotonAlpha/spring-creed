package com.ethan.producer;

import com.ethan.message.Demo07Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class Demo07Producer {
  @Autowired
  RocketMQTemplate mqTemplate;

  public TransactionSendResult sendMessageInTransaction(Integer id) {
    // <1> 创建 Demo07Message 消息
    Message message = MessageBuilder.withPayload(new Demo07Message().setId(id)).build();
    // <2> 发送事务消息
    TransactionSendResult localState = mqTemplate.sendMessageInTransaction(Demo07Message.TOPIC, message, id);
    return localState;
  }



  @RocketMQTransactionListener
  public class TransactionListenerImpl implements RocketMQLocalTransactionListener {
    /**
     * 生产环境可以考虑存储到Redis 或者 MongoDB中
     */
    private ConcurrentHashMap<String, RocketMQLocalTransactionState> localTrans = new ConcurrentHashMap<>();

    /**
     * executeLocalTransaction：本地事务执行方法，系统的业务逻辑（扣钱加钱等事务性操作）
     * @param msg
     * @param arg
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
      // ... local transaction process, return rollback, commit or unknown
      System.out.println("[executeLocalTransaction-1][执行本地事务，消息：{} arg：{}]"+ msg+ arg);
      Demo07Message payload = (Demo07Message) msg.getPayload();
      RocketMQLocalTransactionState state = RocketMQLocalTransactionState.UNKNOWN;

      state = RocketMQLocalTransactionState.COMMIT;
      System.out.println("[执行本地事务 state:{}]"+ state);
      return state;
      /*


      try {
        if (payload.getId() == 123) {
          throw new RuntimeException("illegal");
        }
        state = RocketMQLocalTransactionState.COMMIT;
      } catch (Exception e) {
        log.error("异常，本地回滚");
        state = RocketMQLocalTransactionState.ROLLBACK;
      }
      localTrans.put(payload.getId().toString(), RocketMQLocalTransactionState.UNKNOWN);
      log.info("[executeLocalTransaction][执行本地事务，消息：{} arg：{} state:{}]", msg, arg, state);
      return state;*/
    }

    /**
     * checkLocalTransaction：本地事务状态检查回调方法，模拟从ConcurrentHashMap获取本地事务
     * 1、UNKNOWN； 消息会回调15次，然后扔掉，消费者收不到消息；
     * 2、COMMIT； 消息会在第一次回调后，发送给消费者并被消费；
     * 3、ROLLBACK； 消息会在第一次回调后，扔掉消息，消费者收不到消息。
     * @param msg
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
      // ... check transaction status and return rollback, commit or unknown
      //log.info("[checkLocalTransaction：][回查消息：{}]", msg);
      //return RocketMQLocalTransactionState.COMMIT;
      Demo07Message payload = (Demo07Message) msg.getPayload();
      RocketMQLocalTransactionState state = localTrans.get(payload.getId());
      log.info("[checkLocalTransaction：][回查消息：{} state:{}]", msg, state);
      return state;
    }
  }
}
