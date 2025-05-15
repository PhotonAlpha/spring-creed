package com.ethan.system.mq.producer.permission;

import com.ethan.mq.core.RedisMQTemplate;
import com.ethan.system.mq.message.permission.RoleMenuRefreshMessage;
import com.ethan.system.mq.message.permission.UserRoleRefreshMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Permission 权限相关消息的 Producer
 */
@Component
@Slf4j
public class PermissionProducer {

    @Resource
    private RedisMQTemplate redisMQTemplate;

    /**
     * 发送 {@link RoleMenuRefreshMessage} 消息
     */
    public void sendRoleMenuRefreshMessage() {
        log.info("sendRoleMenuRefreshMessage");
        // RoleMenuRefreshMessage message = new RoleMenuRefreshMessage();
        // redisMQTemplate.send(message);
    }

    /**
     * 发送 {@link UserRoleRefreshMessage} 消息
     */
    public void sendUserRoleRefreshMessage() {
        log.info("sendUserRoleRefreshMessage");
        // UserRoleRefreshMessage message = new UserRoleRefreshMessage();
        // redisMQTemplate.send(message);
    }

}
