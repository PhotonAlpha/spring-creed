package com.ethan.system.convert.sms;

import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.sms.vo.log.SmsLogExcelVO;
import com.ethan.system.controller.admin.sms.vo.log.SmsLogRespVO;
import com.ethan.system.dal.entity.sms.SmsLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 短信日志 Convert
 *
 * 
 */
@Mapper
public interface SmsLogConvert {

    SmsLogConvert INSTANCE = Mappers.getMapper(SmsLogConvert.class);

    SmsLogRespVO convert(SmsLogDO bean);

    List<SmsLogRespVO> convertList(List<SmsLogDO> list);

    PageResult<SmsLogRespVO> convertPage(PageResult<SmsLogDO> page);

    List<SmsLogExcelVO> convertList02(List<SmsLogDO> list);

}
