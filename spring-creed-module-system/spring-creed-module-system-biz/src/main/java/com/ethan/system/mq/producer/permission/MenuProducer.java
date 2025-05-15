package com.ethan.system.mq.producer.permission;

import com.ethan.mq.core.RedisMQTemplate;
import com.ethan.system.mq.message.permission.MenuRefreshMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Menu 菜单相关消息的 Producer
 */
@Component
@Slf4j
public class MenuProducer {

    @Resource
    private RedisMQTemplate redisMQTemplate;

    /**
     * 发送 {@link MenuRefreshMessage} 消息
     */
    public void sendMenuRefreshMessage() {
        log.info("sendMenuRefreshMessage");
        // MenuRefreshMessage message = new MenuRefreshMessage();
        // redisMQTemplate.send(message);
    }

}
