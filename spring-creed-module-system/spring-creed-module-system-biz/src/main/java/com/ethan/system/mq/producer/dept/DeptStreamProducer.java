package com.ethan.system.mq.producer.dept;

import com.ethan.mq.core.RedisMQTemplate;
import com.ethan.system.mq.message.dept.DeptRefreshMessage;
import com.ethan.system.mq.message.dept.DeptRefreshStreamMessage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * Dept 部门相关消息的 Producer
 */
@Component
public class DeptStreamProducer {

    @Resource
    private RedisMQTemplate redisMQTemplate;

    /**
     * 发送 {@link DeptRefreshMessage} 消息
     */
    public void sendDeptRefreshMessage() {
        DeptRefreshStreamMessage message = new DeptRefreshStreamMessage();
        message.addHeader("key", "val");
        redisMQTemplate.send(message);
    }

}
