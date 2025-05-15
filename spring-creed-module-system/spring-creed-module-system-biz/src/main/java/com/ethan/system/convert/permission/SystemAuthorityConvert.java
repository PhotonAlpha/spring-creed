package com.ethan.system.convert.permission;

import com.ethan.common.converter.BasicConvert;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.date.DateUtils;
import com.ethan.security.websecurity.constant.DataScopeEnum;
import com.ethan.system.controller.admin.permission.vo.authority.AuthorityPageReqVO;
import com.ethan.system.controller.admin.permission.vo.authority.AuthorityRespVO;
import com.ethan.system.controller.admin.permission.vo.authority.AuthoritySaveReqVO;
import com.ethan.system.controller.admin.permission.vo.authority.AuthoritySimpleRespVO;
import com.ethan.system.dal.entity.permission.SystemAuthorities;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(imports = {DateUtils.class, DataScopeEnum.class})
public interface SystemAuthorityConvert extends BasicConvert {

    SystemAuthorityConvert INSTANCE = Mappers.getMapper(SystemAuthorityConvert.class);

    @Mapping(target = "createTime", ignore = true)
    SystemAuthorities convert(AuthorityPageReqVO bean);

    SystemAuthorities convert(AuthoritySaveReqVO bean);

    @Mapping(target = "id", ignore = true)
    void update(AuthoritySaveReqVO bean, @MappingTarget SystemAuthorities roles);

    AuthoritySaveReqVO convert0(SystemAuthorities bean);

    AuthorityRespVO convert(SystemAuthorities bean);

    List<AuthoritySimpleRespVO> convertList(List<SystemAuthorities> list);

    List<AuthorityRespVO> convertList03(List<SystemAuthorities> list);

    PageResult<AuthorityRespVO> convert(PageResult<SystemAuthorities> bean);

}
