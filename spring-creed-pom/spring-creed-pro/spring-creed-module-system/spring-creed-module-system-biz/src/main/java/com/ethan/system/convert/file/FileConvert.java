package com.ethan.system.convert.file;

import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.file.vo.file.FileRespVO;
import com.ethan.system.dal.entity.file.FileDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FileConvert {

    FileConvert INSTANCE = Mappers.getMapper(FileConvert.class);

    FileRespVO convert(FileDO bean);

    PageResult<FileRespVO> convertPage(PageResult<FileDO> page);

}
