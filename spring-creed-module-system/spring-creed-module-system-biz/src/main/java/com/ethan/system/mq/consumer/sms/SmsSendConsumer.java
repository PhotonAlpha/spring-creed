package com.ethan.system.mq.consumer.sms;

import com.ethan.mq.core.stream.AbstractStreamMessageListener;
import com.ethan.system.mq.message.sms.SmsSendMessage;
import com.ethan.system.service.sms.SmsSendService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 针对 {@link SmsSendMessage} 的消费者
 *
 */
@Component
@Slf4j
public class SmsSendConsumer extends AbstractStreamMessageListener<SmsSendMessage> {

    @Resource
    private SmsSendService smsSendService;

    @Override
    public void onMessage(SmsSendMessage message) {
        log.info("[onMessage][消息内容({})]", message);
        smsSendService.doSendSms(message);
    }

}
