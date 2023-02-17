package com.ethan.web.logger.core.convert;

import com.ethan.web.logger.core.dto.ApiErrorLogCreateReqDTO;
import com.ethan.web.logger.core.entity.ApiErrorLogDO;
import com.ethan.web.logger.core.vo.accesslog.ApiErrorLogRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * API 错误日志 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface ApiErrorLogConvert {

    ApiErrorLogConvert INSTANCE = Mappers.getMapper(ApiErrorLogConvert.class);

    ApiErrorLogRespVO convert(ApiErrorLogDO bean);

    ApiErrorLogDO convert(ApiErrorLogCreateReqDTO bean);

}
