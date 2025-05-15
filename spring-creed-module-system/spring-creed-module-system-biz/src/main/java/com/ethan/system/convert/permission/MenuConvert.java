package com.ethan.system.convert.permission;

import com.ethan.common.converter.BasicConvert;
import com.ethan.common.utils.date.DateUtils;
import com.ethan.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.ethan.system.controller.admin.permission.vo.menu.MenuSaveVO;
import com.ethan.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.ethan.system.dal.entity.permission.SystemMenus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(imports = DateUtils.class)
public interface MenuConvert extends BasicConvert {

    MenuConvert INSTANCE = Mappers.getMapper(MenuConvert.class);

    SystemMenus convert(MenuSaveVO bean);

    @Mapping(target = "id", ignore = true)
    void update(MenuSaveVO updateReqVO, @MappingTarget SystemMenus systemMenus);

    MenuRespVO convert(SystemMenus bean);
    MenuSimpleRespVO convert0(SystemMenus bean);

    List<MenuRespVO> convert(List<SystemMenus> bean);
    List<MenuSimpleRespVO> convert0(List<SystemMenus> bean);
}