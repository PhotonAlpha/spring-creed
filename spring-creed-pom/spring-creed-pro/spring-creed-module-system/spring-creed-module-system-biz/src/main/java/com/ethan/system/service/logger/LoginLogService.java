package com.ethan.system.service.logger;

import com.ethan.common.pojo.PageResult;
import com.ethan.framework.logger.core.dto.LoginLogCreateReqDTO;
import com.ethan.framework.logger.core.vo.loginlog.LoginLogExportReqVO;
import com.ethan.framework.logger.core.vo.loginlog.LoginLogPageReqVO;
import com.ethan.framework.operatelog.entity.LoginLogDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 登录日志 Service 接口
 */
public interface LoginLogService {

    /**
     * 获得登录日志分页
     *
     * @param reqVO 分页条件
     * @return 登录日志分页
     */
    PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO reqVO);

    /**
     * 获得登录日志列表
     *
     * @param reqVO 列表条件
     * @return 登录日志列表
     */
    List<LoginLogDO> getLoginLogList(LoginLogExportReqVO reqVO);

    /**
     * 创建登录日志
     *
     * @param reqDTO 日志信息
     */
    void createLoginLog(@Valid LoginLogCreateReqDTO reqDTO);

}
