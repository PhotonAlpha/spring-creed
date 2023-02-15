/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.web.apilog.core.service;

import com.ethan.web.logger.core.api.ApiErrorLogApi;
import com.ethan.web.logger.core.dto.ApiErrorLogCreateReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;

@RequiredArgsConstructor
public class ApiErrorLogFrameworkServiceImpl implements ApiErrorLogFrameworkService {

    private final ApiErrorLogApi apiErrorLogApi;

    @Override
    @Async
    public void createApiErrorLog(ApiErrorLog apiErrorLog) {
        ApiErrorLogCreateReqDTO reqDTO = new ApiErrorLogCreateReqDTO();
        BeanUtils.copyProperties(apiErrorLog, reqDTO);
        apiErrorLogApi.createApiErrorLog(reqDTO);
    }
}
