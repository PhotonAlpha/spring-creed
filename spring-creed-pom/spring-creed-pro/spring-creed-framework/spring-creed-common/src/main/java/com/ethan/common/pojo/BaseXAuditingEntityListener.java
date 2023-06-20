/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.common.pojo;

import com.ethan.common.utils.WebFrameworkUtils;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
@Slf4j
public class BaseXAuditingEntityListener {
    @PreUpdate
    public void onPreUpdate(Object entity) {
        log.info("calling onPreUpdate");
        // 获取当前的最后修改者
        String lastModifiedBy = WebFrameworkUtils.getLoginUserId() == null ? "unknown" : WebFrameworkUtils.getLoginUserId();
        if (entity instanceof BaseXDO) {
            BaseXDO baseDO = (BaseXDO) entity;
            // 设置最后修改者的值
            baseDO.setUpdater(lastModifiedBy);
            baseDO.setUpdateTime(ZonedDateTime.now());
        }
    }
}
