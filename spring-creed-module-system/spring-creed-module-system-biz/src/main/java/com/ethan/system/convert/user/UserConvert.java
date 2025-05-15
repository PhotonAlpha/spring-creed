package com.ethan.system.convert.user;

import com.ethan.common.converter.BasicConvert;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.common.utils.collection.MapUtils;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserImportExcelVO;
import com.ethan.system.controller.admin.user.vo.user.UserRespVO;
import com.ethan.system.controller.admin.user.vo.user.UserSaveReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.ethan.system.dal.entity.dept.SystemDepts;
import com.ethan.system.dal.entity.dept.SystemPostUsers;
import com.ethan.system.dal.entity.dept.SystemPosts;
import com.ethan.system.dal.entity.permission.SystemUsers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface UserConvert extends BasicConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    @Mapping(target = "loginDate", expression = "java(java.time.Instant.now())")
    SystemUsers convert(UserSaveReqVO reqVO);
    SystemUsers convert(UserImportExcelVO reqVO);
    UserSaveReqVO convert2UserSaveReq(SystemUsers bean);
    @Mapping(source = "enabled", target = "status")
    UserRespVO convert2UserResp(SystemUsers bean);
    UserSimpleRespVO convert2SimpleResp(SystemUsers bean);
    UserProfileRespVO convert2ProfileResp(SystemUsers bean);

    // 特殊：此处不更新密码
    @Mapping(target = "password", ignore = true)
    void update(UserSaveReqVO updateReqVO, @MappingTarget SystemUsers users);
    void update(UserProfileUpdateReqVO updateReqVO, @MappingTarget SystemUsers users);
    void update(UserImportExcelVO updateReqVO, @MappingTarget SystemUsers users);

    default List<UserRespVO> convertList(List<SystemUsers> list, Map<Long, SystemDepts> deptMap) {
        return CollUtils.convertList(list, user -> convert2UserResp(user, deptMap.get(user.getDeptId())));
    }

    default UserRespVO convert2UserResp(SystemUsers user, SystemDepts dept) {
        UserRespVO userVO = convert2UserResp(user);
        if (Objects.nonNull(dept)) {
            userVO.setDeptName(dept.getName());
            userVO.setDeptId(dept.getId());
        }
        if (!CollectionUtils.isEmpty(user.getPostUsers())) {
            Set<Long> postIds = Optional.of(user.getPostUsers()).orElse(Collections.emptyList())
                    .stream().map(SystemPostUsers::getPosts)
                    .map(SystemPosts::getId).collect(Collectors.toSet());
            userVO.setPostIds(postIds);

        }
        return userVO;
    }

    default List<UserSimpleRespVO> convertSimpleList(List<SystemUsers> list, Map<Long, SystemDepts> deptMap) {
        return CollUtils.convertList(list, user -> {
            UserSimpleRespVO userVO = convert2SimpleResp(user);
            MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> userVO.setDeptName(dept.getName()));
            return userVO;
        });
    }

}
