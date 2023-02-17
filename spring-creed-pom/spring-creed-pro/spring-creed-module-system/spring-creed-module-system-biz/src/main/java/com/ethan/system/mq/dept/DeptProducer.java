package com.ethan.system.mq.dept;

import org.springframework.stereotype.Component;

/**
 * Dept 部门相关消息的 Producer
 */
@Component
public class DeptProducer {

/*     @Resource
    private RedisMQTemplate redisMQTemplate; */

    /**
     * 发送 {@link DeptRefreshMessage} 消息
     */
    public void sendDeptRefreshMessage() {
        // DeptRefreshMessage message = new DeptRefreshMessage();
        // redisMQTemplate.send(message);
    }

}
