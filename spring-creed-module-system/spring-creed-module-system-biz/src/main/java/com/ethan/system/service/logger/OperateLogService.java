package com.ethan.system.service.logger;


import com.ethan.common.pojo.PageResult;
import com.ethan.framework.logger.core.dto.OperateLogCreateReqDTO;
import com.ethan.framework.logger.core.vo.operatelog.OperateLogExportReqVO;
import com.ethan.framework.logger.core.vo.operatelog.OperateLogPageReqVO;
import com.ethan.framework.operatelog.entity.OperateLogDO;

import java.util.List;

/**
 * 操作日志 Service 接口
 *
 * 
 */
public interface OperateLogService {

    /**
     * 记录操作日志
     *
     * @param createReqDTO 操作日志请求
     */
    void createOperateLog(OperateLogCreateReqDTO createReqDTO);

    /**
     * 获得操作日志分页列表
     *
     * @param reqVO 分页条件
     * @return 操作日志分页列表
     */
    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO reqVO);

    /**
     * 获得操作日志列表
     *
     * @param reqVO 列表条件
     * @return 日志列表
     */
    List<OperateLogDO> getOperateLogs(OperateLogExportReqVO reqVO);

}
