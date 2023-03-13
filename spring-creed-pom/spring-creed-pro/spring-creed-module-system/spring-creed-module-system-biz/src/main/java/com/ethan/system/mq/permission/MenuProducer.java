package com.ethan.system.mq.permission;

import org.springframework.stereotype.Component;

/**
 * Menu 菜单相关消息的 Producer
 */
@Component
public class MenuProducer {

    // @Resource
    // private RedisMQTemplate redisMQTemplate;

    /**
     * 发送 {@link MenuRefreshMessage} 消息
     */
    public void sendMenuRefreshMessage() {
        // MenuRefreshMessage message = new MenuRefreshMessage();
        // redisMQTemplate.send(message);
    }

}
