package com.ethan.system.mq.consumer.sms;

import com.ethan.mq.core.pubsub.AbstractChannelMessageListener;
import com.ethan.system.mq.message.sms.SmsTemplateRefreshMessage;
import com.ethan.system.service.sms.SmsTemplateService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 针对 {@link SmsTemplateRefreshMessage} 的消费者
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class SmsTemplateRefreshConsumer extends AbstractChannelMessageListener<SmsTemplateRefreshMessage> {

    @Resource
    private SmsTemplateService smsTemplateService;

    @Override
    public void onMessage(SmsTemplateRefreshMessage message) {
        log.info("[onMessage][收到 SmsTemplate 刷新消息]");
        smsTemplateService.initLocalCache();
    }

}
