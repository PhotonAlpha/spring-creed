/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.web.logger.core.convert;


import com.ethan.web.logger.core.dto.ApiAccessLogCreateReqDTO;
import com.ethan.web.logger.core.entity.ApiAccessLogDO;
import com.ethan.web.logger.core.vo.accesslog.ApiAccessLogRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper
public interface ApiAccessLogConvert {
    ApiAccessLogConvert INSTANCE = Mappers.getMapper(ApiAccessLogConvert.class);

    ApiAccessLogRespVO convert(ApiAccessLogDO bean);

    List<ApiAccessLogRespVO> convertList(List<ApiAccessLogDO> list);

    ApiAccessLogDO convert(ApiAccessLogCreateReqDTO bean);
}
