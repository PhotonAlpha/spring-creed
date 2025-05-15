package com.ethan.system.mq.message.dept;

import com.ethan.mq.core.stream.AbstractStreamMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 部门数据刷新 Message
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DeptRefreshStreamMessage extends AbstractStreamMessage {

    @Override
    public String getStreamKey() {
        return "system.dept.stream.refresh";
    }
}
