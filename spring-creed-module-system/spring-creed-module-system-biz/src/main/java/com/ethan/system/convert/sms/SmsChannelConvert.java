package com.ethan.system.convert.sms;

import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.sms.property.SmsChannelProperties;
import com.ethan.system.controller.admin.sms.vo.channel.SmsChannelCreateReqVO;
import com.ethan.system.controller.admin.sms.vo.channel.SmsChannelRespVO;
import com.ethan.system.controller.admin.sms.vo.channel.SmsChannelSimpleRespVO;
import com.ethan.system.controller.admin.sms.vo.channel.SmsChannelUpdateReqVO;
import com.ethan.system.dal.entity.sms.SmsChannelDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 短信渠道 Convert
 *
 * 
 */
@Mapper
public interface SmsChannelConvert {

    SmsChannelConvert INSTANCE = Mappers.getMapper(SmsChannelConvert.class);

    SmsChannelDO convert(SmsChannelCreateReqVO bean);

    SmsChannelDO convert(SmsChannelUpdateReqVO bean);

    SmsChannelRespVO convert(SmsChannelDO bean);

    List<SmsChannelRespVO> convertList(List<SmsChannelDO> list);

    PageResult<SmsChannelRespVO> convertPage(PageResult<SmsChannelDO> page);

    List<SmsChannelProperties> convertList02(List<SmsChannelDO> list);

    List<SmsChannelSimpleRespVO> convertList03(List<SmsChannelDO> list);

}
