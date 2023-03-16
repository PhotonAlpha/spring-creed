package com.ethan.system.mq.consumer.permission;

import com.ethan.mq.core.pubsub.AbstractChannelMessageListener;
import com.ethan.system.mq.message.permission.UserRoleRefreshMessage;
import com.ethan.system.service.permission.PermissionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 针对 {@link UserRoleRefreshMessage} 的消费者
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class UserRoleRefreshConsumer extends AbstractChannelMessageListener<UserRoleRefreshMessage> {

    @Resource
    private PermissionService permissionService;

    @Override
    public void onMessage(UserRoleRefreshMessage message) {
        log.info("[onMessage][收到 User 与 Role 的关联刷新消息]");
        permissionService.initLocalCache();
    }

}
