package com.ethan.system.convert.file;

import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.file.vo.config.FileConfigCreateReqVO;
import com.ethan.system.controller.admin.file.vo.config.FileConfigRespVO;
import com.ethan.system.controller.admin.file.vo.config.FileConfigUpdateReqVO;
import com.ethan.system.dal.entity.file.FileConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 文件配置 Convert
 *
 * 
 */
@Mapper
public interface FileConfigConvert {

    FileConfigConvert INSTANCE = Mappers.getMapper(FileConfigConvert.class);

    // @Mapping(target = "config", ignore = true)
    FileConfigDO convert(FileConfigCreateReqVO bean);

    // @Mapping(target = "config", ignore = true)
    FileConfigDO convert(FileConfigUpdateReqVO bean);

    FileConfigRespVO convert(FileConfigDO bean);

    List<FileConfigRespVO> convertList(List<FileConfigDO> list);

    PageResult<FileConfigRespVO> convertPage(PageResult<FileConfigDO> page);

}
