package com.ethan.system.convert.dict;

import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.dict.vo.type.DictTypeCreateReqVO;
import com.ethan.system.controller.admin.dict.vo.type.DictTypeExcelVO;
import com.ethan.system.controller.admin.dict.vo.type.DictTypeRespVO;
import com.ethan.system.controller.admin.dict.vo.type.DictTypeSimpleRespVO;
import com.ethan.system.controller.admin.dict.vo.type.DictTypeUpdateReqVO;
import com.ethan.system.dal.entity.dict.DictTypeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DictTypeConvert {

    DictTypeConvert INSTANCE = Mappers.getMapper(DictTypeConvert.class);

    PageResult<DictTypeRespVO> convertPage(PageResult<DictTypeDO> bean);

    DictTypeRespVO convert(DictTypeDO bean);

    DictTypeDO convert(DictTypeCreateReqVO bean);

    DictTypeDO convert(DictTypeUpdateReqVO bean);

    List<DictTypeSimpleRespVO> convertList(List<DictTypeDO> list);

    List<DictTypeExcelVO> convertList02(List<DictTypeDO> list);

}
