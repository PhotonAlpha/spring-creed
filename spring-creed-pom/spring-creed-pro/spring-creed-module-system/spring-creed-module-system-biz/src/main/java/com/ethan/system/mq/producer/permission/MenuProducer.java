package com.ethan.system.mq.producer.permission;

import com.ethan.mq.core.RedisMQTemplate;
import com.ethan.system.mq.message.permission.MenuRefreshMessage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * Menu 菜单相关消息的 Producer
 */
@Component
public class MenuProducer {

    @Resource
    private RedisMQTemplate redisMQTemplate;

    /**
     * 发送 {@link MenuRefreshMessage} 消息
     */
    public void sendMenuRefreshMessage() {
        MenuRefreshMessage message = new MenuRefreshMessage();
        redisMQTemplate.send(message);
    }

}
