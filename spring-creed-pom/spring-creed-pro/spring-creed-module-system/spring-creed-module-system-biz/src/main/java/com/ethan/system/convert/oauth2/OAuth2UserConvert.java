package com.ethan.system.convert.oauth2;

import com.ethan.common.constant.SexEnum;
import com.ethan.common.converter.BasicConvert;
import com.ethan.system.controller.admin.oauth2.vo.user.OAuth2UserInfoRespVO;
import com.ethan.system.controller.admin.oauth2.vo.user.OAuth2UserUpdateReqVO;
import com.ethan.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.ethan.system.dal.entity.dept.SystemDepts;
import com.ethan.system.dal.entity.dept.SystemPosts;
import com.ethan.system.dal.entity.permission.SystemUsers;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OAuth2UserConvert extends BasicConvert {

    OAuth2UserConvert INSTANCE = Mappers.getMapper(OAuth2UserConvert.class);

    OAuth2UserInfoRespVO convert(SystemUsers bean);

    default Integer map(SexEnum value) {
        return value.getSex();
    }

    OAuth2UserInfoRespVO.Dept convert(SystemDepts dept);
    List<OAuth2UserInfoRespVO.Post> convertList(List<SystemPosts> list);

    UserProfileUpdateReqVO convert(OAuth2UserUpdateReqVO bean);

}
