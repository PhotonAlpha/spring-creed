package com.ethan.identity.utils;

import com.ethan.common.utils.ApplicationContextHolder;
import com.ethan.identity.core.common.Result;
import com.ethan.identity.core.common.Status;
import com.ethan.identity.server.exception.LeafServerException;
import com.ethan.identity.server.exception.NoKeyException;
import com.ethan.identity.server.service.SegmentService;
import com.ethan.identity.server.service.SnowflakeService;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 31/12/24
 */
@UtilityClass
@Slf4j
public class IDGenerator {
    public String getSegmentId(final String key) {
        try {
            var segmentService = ApplicationContextHolder.getBean(SegmentService.class);
            var result = segmentService.getId(key);
            if (result.getStatus().equals(Status.SUCCESS)) {
                return String.valueOf(result.getId());
            }
        } catch (Exception e) {
            log.error("SegmentIDGen not initiated.");
        }
        return null;
    }
    public String getSnowflakeId(final String key) {
        try {
            var snowflakeService = ApplicationContextHolder.getBean(SnowflakeService.class);
            var result = snowflakeService.getId(key);
            if (result.getStatus().equals(Status.SUCCESS)) {
                return String.valueOf(result.getId());
            }
        } catch (Exception e) {
            log.error("SnowflakeIDGen not initiated.");
        }
        return null;
    }
}
