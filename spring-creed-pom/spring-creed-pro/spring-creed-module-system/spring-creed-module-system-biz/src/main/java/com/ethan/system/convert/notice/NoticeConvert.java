package com.ethan.system.convert.notice;

import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.notice.vo.NoticeCreateReqVO;
import com.ethan.system.controller.admin.notice.vo.NoticeRespVO;
import com.ethan.system.controller.admin.notice.vo.NoticeUpdateReqVO;
import com.ethan.system.dal.entity.notice.NoticeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper
public interface NoticeConvert {

    NoticeConvert INSTANCE = Mappers.getMapper(NoticeConvert.class);

    PageResult<NoticeRespVO> convertPage(PageResult<NoticeDO> page);

    NoticeRespVO convert(NoticeDO bean);

    NoticeDO convert(NoticeUpdateReqVO bean);

    NoticeDO convert(NoticeCreateReqVO bean);

    default LocalDateTime asLdt(Instant date) {
        return LocalDateTime.ofInstant(date, ZoneId.systemDefault());
    }
    default Instant asInstant(LocalDateTime ldt) {
        return ldt.atZone(ZoneId.systemDefault()).toInstant();
    }
}
