package com.ethan.system.convert.notify;

import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.notify.vo.template.NotifyTemplateCreateReqVO;
import com.ethan.system.controller.admin.notify.vo.template.NotifyTemplateRespVO;
import com.ethan.system.controller.admin.notify.vo.template.NotifyTemplateUpdateReqVO;
import com.ethan.system.dal.entity.notify.NotifyTemplateDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 站内信模版 Convert
 *
 * @author xrcoder
 */
@Mapper
public interface NotifyTemplateConvert {

    NotifyTemplateConvert INSTANCE = Mappers.getMapper(NotifyTemplateConvert.class);

    NotifyTemplateDO convert(NotifyTemplateCreateReqVO bean);

    NotifyTemplateDO convert(NotifyTemplateUpdateReqVO bean);

    NotifyTemplateRespVO convert(NotifyTemplateDO bean);

    List<NotifyTemplateRespVO> convertList(List<NotifyTemplateDO> list);

    PageResult<NotifyTemplateRespVO> convertPage(PageResult<NotifyTemplateDO> page);

}
