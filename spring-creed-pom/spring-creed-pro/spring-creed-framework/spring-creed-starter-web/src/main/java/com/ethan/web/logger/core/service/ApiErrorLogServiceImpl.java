package com.ethan.web.logger.core.service;

import com.ethan.web.logger.core.constant.ApiErrorLogProcessStatusEnum;
import com.ethan.web.logger.core.convert.ApiErrorLogConvert;
import com.ethan.web.logger.core.dto.ApiErrorLogCreateReqDTO;
import com.ethan.web.logger.core.entity.ApiErrorLogDO;
import com.ethan.web.logger.core.repository.ApiErrorLogRepository;
import com.ethan.web.logger.core.vo.accesslog.ApiErrorLogPageReqVO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * API 错误日志 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ApiErrorLogServiceImpl implements ApiErrorLogService {
    private static final Logger log = LoggerFactory.getLogger(ApiErrorLogServiceImpl.class);
    @Resource
    private ApiErrorLogRepository apiErrorRepository;

    @Override
    public void createApiErrorLog(ApiErrorLogCreateReqDTO createDTO) {
        ApiErrorLogDO apiErrorLog = ApiErrorLogConvert.INSTANCE.convert(createDTO);
        apiErrorLog.setProcessStatus(ApiErrorLogProcessStatusEnum.INIT.getStatus());
        apiErrorRepository.save(apiErrorLog);
    }

    /**
     * ref {@link ApiAccessLogServiceImpl}
     * @param pageReqVO 分页查询
     * @return
     */
    @Override
    public Page<ApiErrorLogDO> getApiErrorLogPage(ApiErrorLogPageReqVO pageReqVO) {
        return null;
    }

    @Override
    public List<ApiErrorLogDO> getApiErrorLogList(ApiErrorLogPageReqVO exportReqVO) {
        return null;
    }

    @Override
    public void updateApiErrorLogProcess(Long id, Integer processStatus, Long processUserId) {

    }
}
