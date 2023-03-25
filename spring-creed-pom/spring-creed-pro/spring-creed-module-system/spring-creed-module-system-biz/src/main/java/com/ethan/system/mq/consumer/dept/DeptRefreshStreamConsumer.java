package com.ethan.system.mq.consumer.dept;

import com.ethan.mq.core.stream.AbstractStreamMessageListener;
import com.ethan.system.mq.message.dept.DeptRefreshMessage;
import com.ethan.system.mq.message.dept.DeptRefreshStreamMessage;
import com.ethan.system.service.dept.DeptService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 针对 {@link DeptRefreshMessage} 的消费者
 *
 */
@Component
@Slf4j
public class DeptRefreshStreamConsumer extends AbstractStreamMessageListener<DeptRefreshStreamMessage> {

    @Resource
    private DeptService deptService;

    @Override
    public void onMessage(DeptRefreshStreamMessage message) {
        log.info("[onMessage][收到 Dept 刷新消息]{}", message);
    }
}
