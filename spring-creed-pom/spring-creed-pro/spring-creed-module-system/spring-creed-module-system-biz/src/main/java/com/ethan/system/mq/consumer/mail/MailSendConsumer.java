package com.ethan.system.mq.consumer.mail;

import com.ethan.mq.core.stream.AbstractStreamMessageListener;
import com.ethan.system.mq.message.mail.MailSendMessage;
import lombok.extern.slf4j.Slf4j;

// TODO 这个暂未实现
// @Component
@Slf4j
public class MailSendConsumer extends AbstractStreamMessageListener<MailSendMessage> {

    @Override
    public void onMessage(MailSendMessage message) {
        log.info("[onMessage][消息内容({})]", message);
    }

}
