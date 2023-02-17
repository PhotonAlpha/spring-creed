package com.ethan.web.operatelog.converter;

import com.ethan.web.operatelog.entity.OperateLogDO;
import com.ethan.web.operatelog.service.OperateLog;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OperateLogConvert {

    OperateLogConvert INSTANCE = Mappers.getMapper(OperateLogConvert.class);

    OperateLogDO convert(OperateLog operateLog);
}
