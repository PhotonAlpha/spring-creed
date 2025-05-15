package com.ethan.framework.operatelog.converter;

import com.ethan.common.pojo.PageResult;
import com.ethan.framework.logger.core.dto.LoginLogCreateReqDTO;
import com.ethan.framework.logger.core.vo.loginlog.LoginLogExcelVO;
import com.ethan.framework.logger.core.vo.loginlog.LoginLogRespVO;
import com.ethan.framework.operatelog.entity.LoginLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface LoginLogConvert {

    LoginLogConvert INSTANCE = Mappers.getMapper(LoginLogConvert.class);

    PageResult<LoginLogRespVO> convertPage(PageResult<LoginLogDO> page);

    List<LoginLogExcelVO> convertList(List<LoginLogDO> list);

    LoginLogDO convert(LoginLogCreateReqDTO bean);

}
