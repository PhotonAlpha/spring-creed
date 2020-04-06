package com.ethan.mapper;

import com.ethan.entity.BloggerDO;
import com.ethan.entity.GroupDO;
import com.ethan.entity.RoleDO;
import com.ethan.vo.BloggerVO;
import com.ethan.vo.GroupVO;
import com.ethan.vo.RoleVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BloggerMapper {

  // @Mapping(source = "bloggerId", target = "bloggerId")
  BloggerVO bloggerToVo(BloggerDO aDo);

  GroupVO groupToVo(GroupDO groupDO);

  List<GroupVO> groupToVos(List<GroupDO> groups);

  RoleVO roleToVo(RoleDO roleDO);

  List<RoleVO> roleToVos(List<RoleDO> groups);
}
