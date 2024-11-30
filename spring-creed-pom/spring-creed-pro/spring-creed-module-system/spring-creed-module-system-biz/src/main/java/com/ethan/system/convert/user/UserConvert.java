package com.ethan.system.convert.user;

import com.ethan.common.constant.SexEnum;
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
import com.ethan.system.dal.entity.permission.SystemUsers;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserConvert extends BasicConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    SystemUsers convert(UserSaveReqVO reqVO);
    SystemUsers convert(UserImportExcelVO reqVO);
    UserSaveReqVO convert2UserSaveReq(SystemUsers bean);
    UserRespVO convert2UserResp(SystemUsers bean);
    UserSimpleRespVO convert2SimpleResp(SystemUsers bean);
    UserProfileRespVO convert2ProfileResp(SystemUsers bean);

    void update(UserSaveReqVO updateReqVO, @MappingTarget SystemUsers users);
    void update(UserProfileUpdateReqVO updateReqVO, @MappingTarget SystemUsers users);
    void update(UserImportExcelVO updateReqVO, @MappingTarget SystemUsers users);

    default List<UserRespVO> convertList(List<SystemUsers> list, Map<Long, SystemDepts> deptMap) {
        return CollUtils.convertList(list, user -> convert2UserSaveReq(user, deptMap.get(user.getDeptId())));
    }

    default UserRespVO convert2UserSaveReq(SystemUsers user, SystemDepts dept) {
        UserRespVO userVO = convert2UserResp(user);
        if (dept != null) {
            userVO.setDeptName(dept.getName());
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
