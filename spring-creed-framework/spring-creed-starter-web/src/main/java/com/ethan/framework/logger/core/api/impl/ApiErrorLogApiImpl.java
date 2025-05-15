/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.framework.logger.core.api.impl;

import com.ethan.framework.logger.core.dto.ApiErrorLogCreateReqDTO;
import com.ethan.framework.logger.core.service.ApiErrorLogService;
import com.ethan.framework.logger.core.api.ApiErrorLogApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiErrorLogApiImpl implements ApiErrorLogApi {
    @Resource
    private ApiErrorLogService apiErrorLogService;
    @Override
    public void createApiErrorLog(ApiErrorLogCreateReqDTO createDTO) {
        apiErrorLogService.createApiErrorLog(createDTO);
    }
}
