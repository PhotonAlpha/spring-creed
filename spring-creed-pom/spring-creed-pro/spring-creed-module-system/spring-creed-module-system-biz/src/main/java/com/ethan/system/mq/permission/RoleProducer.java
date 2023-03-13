package com.ethan.system.mq.permission;

import org.springframework.stereotype.Component;

/**
 * Role 角色相关消息的 Producer
 *
 * 
 */
@Component
public class RoleProducer {

    // @Resource
    // private RedisMQTemplate redisMQTemplate;

    /**
     * 发送 {@link RoleRefreshMessage} 消息
     */
    public void sendRoleRefreshMessage() {
        // RoleRefreshMessage message = new RoleRefreshMessage();
        // redisMQTemplate.send(message);
    }

}
