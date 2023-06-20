package com.ethan.system.convert.user;

import com.ethan.common.constant.SexEnum;
import com.ethan.security.websecurity.entity.CreedUser;
import com.ethan.system.controller.admin.user.dto.AdminUserRespDTO;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserCreateReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserExcelVO;
import com.ethan.system.controller.admin.user.vo.user.UserImportExcelVO;
import com.ethan.system.controller.admin.user.vo.user.UserPageItemRespVO;
import com.ethan.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.ethan.system.controller.admin.user.vo.user.UserUpdateReqVO;
import com.ethan.system.dal.entity.dept.DeptDO;
import com.ethan.system.dal.entity.dept.PostDO;
import com.ethan.system.dal.entity.permission.RoleDO;
import com.ethan.system.dal.entity.social.SocialUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    UserPageItemRespVO convert(CreedUser bean);

    default Integer map(SexEnum value) {
        return value.getSex();
    }


    UserPageItemRespVO.Dept convert(DeptDO bean);

    CreedUser convert(UserCreateReqVO bean);

    default SexEnum mapSex(Integer value) {
        return SexEnum.findByValue(value);
    }

    CreedUser convert(UserUpdateReqVO bean);

    UserExcelVO convert02(CreedUser bean);

    CreedUser convert(UserImportExcelVO bean);

    void update(UserImportExcelVO bean, @MappingTarget CreedUser consumer);

    UserProfileRespVO convert03(CreedUser bean);

    List<UserProfileRespVO.Role> convertList(List<RoleDO> list);

    UserProfileRespVO.Dept convert02(DeptDO bean);

    CreedUser convert(UserProfileUpdateReqVO bean);

    CreedUser convert(UserProfileUpdatePasswordReqVO bean);

    List<UserProfileRespVO.Post> convertList02(List<PostDO> list);

    List<UserProfileRespVO.SocialUser> convertList03(List<SocialUserDO> list);

    List<UserSimpleRespVO> convertList04(List<CreedUser> list);

    AdminUserRespDTO convert4(CreedUser bean);

    List<AdminUserRespDTO> convertList4(List<CreedUser> users);

}
