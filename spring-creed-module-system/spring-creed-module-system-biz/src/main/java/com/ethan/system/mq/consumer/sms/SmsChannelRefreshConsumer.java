package com.ethan.system.mq.consumer.sms;

import com.ethan.mq.core.pubsub.AbstractChannelMessageListener;
import com.ethan.system.mq.message.sms.SmsChannelRefreshMessage;
import com.ethan.system.service.sms.SmsChannelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 针对 {@link SmsChannelRefreshMessage} 的消费者
 *
 */
@Component
@Slf4j
public class SmsChannelRefreshConsumer extends AbstractChannelMessageListener<SmsChannelRefreshMessage> {

    @Resource
    private SmsChannelService smsChannelService;

    @Override
    public void onMessage(SmsChannelRefreshMessage message) {
        log.info("[onMessage][收到 SmsChannel 刷新消息]");
        smsChannelService.initSmsClients();
    }

}
