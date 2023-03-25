package com.ethan.framework.logger.core.service;

import com.ethan.framework.logger.core.dto.ApiAccessLogCreateReqDTO;
import com.ethan.framework.logger.core.entity.ApiAccessLogDO;
import com.ethan.framework.logger.core.vo.accesslog.ApiAccessLogPageReqVO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * API 访问日志 Service 接口
 *
 * 
 */
public interface ApiAccessLogService {

    /**
     * 创建 API 访问日志
     *
     * @param createReqDTO API 访问日志
     */
    void createApiAccessLog(ApiAccessLogCreateReqDTO createReqDTO);

    /**
     * 获得 API 访问日志分页
     *
     * @param pageReqVO 分页查询
     * @return API 访问日志分页
     */
    Page<ApiAccessLogDO> getApiAccessLogPage(ApiAccessLogPageReqVO pageReqVO);

    /**
     * 获得 API 访问日志列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return API 访问日志分页
     */
    List<ApiAccessLogDO> getApiAccessLogList(ApiAccessLogPageReqVO exportReqVO);

}
