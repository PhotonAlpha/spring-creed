package com.ethan.framework.logger.core.convert;

import com.ethan.framework.logger.core.dto.ApiErrorLogCreateReqDTO;
import com.ethan.framework.logger.core.entity.ApiErrorLogDO;
import com.ethan.framework.logger.core.vo.accesslog.ApiErrorLogRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * API 错误日志 Convert
 *
 * 
 */
@Mapper
public interface ApiErrorLogConvert {

    ApiErrorLogConvert INSTANCE = Mappers.getMapper(ApiErrorLogConvert.class);

    ApiErrorLogRespVO convert(ApiErrorLogDO bean);

    ApiErrorLogDO convert(ApiErrorLogCreateReqDTO bean);

}
