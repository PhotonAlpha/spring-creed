package com.ethan.system.mq.message.dept;

import com.ethan.mq.core.pubsub.AbstractChannelMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门数据刷新 Message
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeptRefreshMessage extends AbstractChannelMessage {

    @Override
    public String getChannel() {
        return "system.dept.refresh";
    }

}
