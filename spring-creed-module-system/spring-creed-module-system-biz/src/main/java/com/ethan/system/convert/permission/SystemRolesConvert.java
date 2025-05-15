package com.ethan.system.convert.permission;

import com.ethan.common.converter.BasicConvert;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.date.DateUtils;
import com.ethan.security.websecurity.constant.DataScopeEnum;
import com.ethan.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleRespVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import com.ethan.system.dal.entity.permission.SystemRoles;
import com.ethan.system.service.permission.bo.RoleCreateReqBO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(imports = {DateUtils.class, DataScopeEnum.class})
public interface SystemRolesConvert extends BasicConvert {

    SystemRolesConvert INSTANCE = Mappers.getMapper(SystemRolesConvert.class);

    @Mapping(target = "createTime", ignore = true)
    SystemRoles convert(RolePageReqVO bean);

    @Mapping(target = "dataScope", expression = "java(DataScopeEnum.ALL)")
        // 默认可查看所有数据。原因是，可能一些项目不需要项目权限
    SystemRoles convert(RoleSaveReqVO bean);

    @Mapping(target = "id", ignore = true)
    void update(RoleSaveReqVO bean, @MappingTarget SystemRoles roles);

    RoleSaveReqVO convert0(SystemRoles bean);

    @Mapping(source = "enabled", target = "status")
    RoleRespVO convert(SystemRoles bean);

    List<RoleSimpleRespVO> convertList(List<SystemRoles> list);

    List<RoleRespVO> convertList03(List<SystemRoles> list);

    SystemRoles convert(RoleCreateReqBO bean);

    PageResult<RoleRespVO> convert(PageResult<SystemRoles> bean);

}
