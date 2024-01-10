package com.ethan.system.convert.permission;

import com.ethan.system.controller.admin.permission.vo.menu.MenuCreateReqVO;
import com.ethan.system.controller.admin.permission.vo.menu.MenuVO;
import com.ethan.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.ethan.system.controller.admin.permission.vo.menu.MenuUpdateReqVO;
import com.ethan.system.dal.entity.permission.MenuDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MenuConvert {

    MenuConvert INSTANCE = Mappers.getMapper(MenuConvert.class);

    List<MenuVO> convertList(List<MenuDO> list);

    MenuDO convert(MenuCreateReqVO bean);

    MenuDO convert(MenuUpdateReqVO bean);

    MenuVO convert(MenuDO bean);

    List<MenuSimpleRespVO> convertList02(List<MenuDO> list);

}
