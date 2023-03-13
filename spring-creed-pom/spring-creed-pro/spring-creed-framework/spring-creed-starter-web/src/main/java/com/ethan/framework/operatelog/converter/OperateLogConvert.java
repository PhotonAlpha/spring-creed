package com.ethan.framework.operatelog.converter;

import com.ethan.framework.logger.core.dto.OperateLogCreateReqDTO;
import com.ethan.framework.operatelog.entity.OperateLogDO;
import com.ethan.framework.operatelog.service.OperateLog;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OperateLogConvert {

    OperateLogConvert INSTANCE = Mappers.getMapper(OperateLogConvert.class);

    OperateLogDO convert(OperateLog operateLog);
    OperateLogDO convert(OperateLogCreateReqDTO operateLog);
}
