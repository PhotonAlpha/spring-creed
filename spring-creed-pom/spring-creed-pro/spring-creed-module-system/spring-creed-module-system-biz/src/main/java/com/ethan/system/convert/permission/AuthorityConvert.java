package com.ethan.system.convert.permission;

import com.ethan.security.websecurity.entity.CreedAuthorities;
import com.ethan.system.controller.admin.permission.vo.role.RoleCreateReqVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleExcelVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleRespVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleUpdateReqVO;
import com.ethan.system.service.permission.bo.RoleCreateReqBO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AuthorityConvert {

    AuthorityConvert INSTANCE = Mappers.getMapper(AuthorityConvert.class);

    @Mapping(target = "authority", expression = "java(org.apache.commons.lang3.StringUtils.upperCase(bean.getAuthority()))")
    void update(RoleUpdateReqVO bean, @MappingTarget CreedAuthorities authorities);

    RoleRespVO convert(CreedAuthorities bean);

    @Mapping(target = "authority", expression = "java(org.apache.commons.lang3.StringUtils.upperCase(bean.getAuthority()))")
    CreedAuthorities convert(RoleCreateReqVO bean);

    List<RoleSimpleRespVO> convertList02(List<CreedAuthorities> list);

    List<RoleExcelVO> convertList03(List<CreedAuthorities> list);

    CreedAuthorities convert(RoleCreateReqBO bean);

}
