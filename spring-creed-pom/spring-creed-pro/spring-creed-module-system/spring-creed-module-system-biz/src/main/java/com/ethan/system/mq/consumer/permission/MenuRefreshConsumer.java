package com.ethan.system.mq.consumer.permission;

import com.ethan.mq.core.pubsub.AbstractChannelMessageListener;
import com.ethan.system.mq.message.permission.MenuRefreshMessage;
import com.ethan.system.service.permission.MenuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 针对 {@link MenuRefreshMessage} 的消费者
 *
 */
@Component
@Slf4j
public class MenuRefreshConsumer extends AbstractChannelMessageListener<MenuRefreshMessage> {

    @Resource
    private MenuService menuService;

    @Override
    public void onMessage(MenuRefreshMessage message) {
        log.info("[onMessage][收到 Menu 刷新消息]");
        menuService.initLocalCache();
    }

}
