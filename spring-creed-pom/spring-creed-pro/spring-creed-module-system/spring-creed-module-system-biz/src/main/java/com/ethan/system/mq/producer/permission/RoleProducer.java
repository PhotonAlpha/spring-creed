package com.ethan.system.mq.producer.permission;

import com.ethan.mq.core.RedisMQTemplate;
import com.ethan.system.mq.message.permission.RoleRefreshMessage;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Role 角色相关消息的 Producer
 *
 * 
 */
@Component
public class RoleProducer {
    private static final Logger log = LoggerFactory.getLogger(RoleProducer.class);
    @Resource
    private RedisMQTemplate redisMQTemplate;

    /**
     * 发送 {@link RoleRefreshMessage} 消息
     */
    public void sendRoleRefreshMessage() {
        log.info("sendRoleRefreshMessage");
        RoleRefreshMessage message = new RoleRefreshMessage();
        redisMQTemplate.send(message);
    }

}
