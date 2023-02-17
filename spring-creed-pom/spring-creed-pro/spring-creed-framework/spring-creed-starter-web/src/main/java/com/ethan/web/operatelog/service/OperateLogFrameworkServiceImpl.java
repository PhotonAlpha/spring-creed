package com.ethan.web.operatelog.service;

import com.ethan.web.operatelog.converter.OperateLogConvert;
import com.ethan.web.operatelog.entity.OperateLogDO;
import com.ethan.web.operatelog.repository.OperateLogRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;

/**
 * 操作日志 Framework Service 实现类
 *
 * 基于 {@link OperateLogApi} 实现，记录操作日志
 *
 * @author 芋道源码
 */
@RequiredArgsConstructor
public class OperateLogFrameworkServiceImpl implements OperateLogFrameworkService {

    private final OperateLogRepository operateLogRepository;

    @Override
    @Async
    public void createOperateLog(OperateLog operateLog) {
        OperateLogDO logDO = OperateLogConvert.INSTANCE.convert(operateLog);
        logDO.setJavaMethodArgs(StringUtils.abbreviate(logDO.getJavaMethodArgs(), OperateLogDO.JAVA_METHOD_ARGS_MAX_LENGTH));
        logDO.setResultData(StringUtils.abbreviate(logDO.getResultData(), OperateLogDO.RESULT_MAX_LENGTH));
        operateLogRepository.save(logDO);
    }

}
