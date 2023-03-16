package com.ethan.system.mq.producer.dept;

import com.ethan.mq.core.RedisMQTemplate;
import com.ethan.system.mq.message.dept.DeptRefreshMessage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * Dept 部门相关消息的 Producer
 */
@Component
public class DeptProducer {

    @Resource
    private RedisMQTemplate redisMQTemplate;

    /**
     * 发送 {@link DeptRefreshMessage} 消息
     */
    public void sendDeptRefreshMessage() {
        DeptRefreshMessage message = new DeptRefreshMessage();
        redisMQTemplate.send(message);
    }

}
