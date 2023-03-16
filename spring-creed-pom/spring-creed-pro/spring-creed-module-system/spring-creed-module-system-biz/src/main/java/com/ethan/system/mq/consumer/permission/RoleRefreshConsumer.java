package com.ethan.system.mq.consumer.permission;

import com.ethan.mq.core.pubsub.AbstractChannelMessageListener;
import com.ethan.system.mq.message.permission.RoleRefreshMessage;
import com.ethan.system.service.permission.RoleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 针对 {@link RoleRefreshMessage} 的消费者
 *
 */
@Component
@Slf4j
public class RoleRefreshConsumer extends AbstractChannelMessageListener<RoleRefreshMessage> {

    @Resource
    private RoleService roleService;

    @Override
    public void onMessage(RoleRefreshMessage message) {
        log.info("[onMessage][收到 Role 刷新消息]");
        roleService.initLocalCache();
    }

}
