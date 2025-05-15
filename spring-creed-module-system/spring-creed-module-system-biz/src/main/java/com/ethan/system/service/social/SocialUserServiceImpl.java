package com.ethan.system.service.social;

import com.ethan.system.controller.admin.social.dto.SocialUserBindReqDTO;
import com.ethan.system.dal.entity.social.SocialUserBindDO;
import com.ethan.system.dal.entity.social.SocialUserDO;
import com.ethan.system.dal.repository.social.SocialUserBindRepository;
import com.ethan.system.dal.repository.social.SocialUserRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.common.utils.collection.CollUtils.convertSet;
import static com.ethan.system.constant.ErrorCodeConstants.AUTH_THIRD_LOGIN_NOT_BIND;
import static com.ethan.system.constant.ErrorCodeConstants.SOCIAL_USER_NOT_FOUND;


/**
 * 社交用户 Service 实现类
 *
 * 
 */
@Service
@Validated
@Slf4j
public class SocialUserServiceImpl implements SocialUserService {

    // @Resource// 由于自定义了 YudaoAuthRequestFactory 无法覆盖默认的 AuthRequestFactory，所以只能注入它
    // private YudaoAuthRequestFactory yudaoAuthRequestFactory;

    @Resource
    private SocialUserBindRepository socialUserBindRepository;
    @Resource
    private SocialUserRepository socialUserRepository;

    @Override
    public String getAuthorizeUrl(Integer type, String redirectUri) {
        // 获得对应的 AuthRequest 实现
        // AuthRequest authRequest = yudaoAuthRequestFactory.get(SocialTypeEnum.valueOfType(type).getSource());
        // 生成跳转地址
        // String authorizeUri = authRequest.authorize(AuthStateUtils.createState());
        // return HttpUtils.replaceUrlQuery(authorizeUri, "redirect_uri", redirectUri);
        return null;
    }

    @Override
    public SocialUserDO authSocialUser(Integer type, String code, String state) {
        // 优先从 DB 中获取，因为 code 有且可以使用一次。
        // 在社交登录时，当未绑定 User 时，需要绑定登录，此时需要 code 使用两次
        SocialUserDO socialUser = socialUserRepository.findByTypeAndCodeAndState(type, code, state);
        if (socialUser != null) {
            return socialUser;
        }

        // 请求获取
        // AuthUser authUser = getAuthUser(type, code, state);
        // Assert.notNull(authUser, "三方用户不能为空");

        // 保存到 DB 中
        socialUser = socialUserRepository.findByTypeAndOpenid(type, "authUser.getUuid()");
        if (socialUser == null) {
            socialUser = new SocialUserDO();
        }
        // socialUser.setType(type).setCode(code).setState(state) // 需要保存 code + state 字段，保证后续可查询
        //         .setOpenid(authUser.getUuid()).setToken(authUser.getToken().getAccessToken()).setRawTokenInfo((toJsonString(authUser.getToken())))
        //         .setNickname(authUser.getNickname()).setAvatar(authUser.getAvatar()).setRawUserInfo(toJsonString(authUser.getRawUserInfo()));
        if (socialUser.getId() == null) {
            socialUserRepository.save(socialUser);
        } else {
            socialUserRepository.save(socialUser);
        }
        return socialUser;
    }

    @Override
    public List<SocialUserDO> getSocialUserList(String userId, Integer userType) {
        // 获得绑定
        List<SocialUserBindDO> socialUserBinds = socialUserBindRepository.findByUserIdAndUserType(userId, userType);
        if (CollectionUtils.isEmpty(socialUserBinds)) {
            return Collections.emptyList();
        }
        // 获得社交用户
        return socialUserRepository.findAllById(convertSet(socialUserBinds, SocialUserBindDO::getSocialUserId));
    }

    @Override
    @Transactional
    public void bindSocialUser(SocialUserBindReqDTO reqDTO) {
        // 获得社交用户
        SocialUserDO socialUser = authSocialUser(reqDTO.getType(), reqDTO.getCode(), reqDTO.getState());
        Assert.notNull(socialUser, "社交用户不能为空");

        // 社交用户可能之前绑定过别的用户，需要进行解绑
        socialUserBindRepository.deleteByUserTypeAndSocialUserId(reqDTO.getUserType(), socialUser.getId());

        // 用户可能之前已经绑定过该社交类型，需要进行解绑
        socialUserBindRepository.deleteByUserTypeAndUserIdAndSocialType(reqDTO.getUserType(), reqDTO.getUserId() + "",
                socialUser.getType());

        // 绑定当前登录的社交用户
        SocialUserBindDO socialUserBind = SocialUserBindDO.builder()
                .userId(reqDTO.getUserId()+"").userType(reqDTO.getUserType())
                .socialUserId(socialUser.getId()).socialType(socialUser.getType()).build();
        socialUserBindRepository.save(socialUserBind);
    }

    @Override
    public void unbindSocialUser(String userId, Integer userType, Integer type, String openid) {
        // 获得 openid 对应的 SocialUserDO 社交用户
        SocialUserDO socialUser = socialUserRepository.findByTypeAndOpenid(type, openid);
        if (socialUser == null) {
            throw exception(SOCIAL_USER_NOT_FOUND);
        }

        // 获得对应的社交绑定关系
        socialUserBindRepository.deleteByUserTypeAndUserIdAndSocialType(userType, userId, socialUser.getType());
    }

    @Override
    public String getBindUserId(Integer userType, Integer type, String code, String state) {
        // 获得社交用户
        SocialUserDO socialUser = authSocialUser(type, code, state);
        Assert.notNull(socialUser, "社交用户不能为空");

        // 如果未绑定的社交用户，则无法自动登录，进行报错
        SocialUserBindDO socialUserBind = socialUserBindRepository.findByUserTypeAndSocialUserId(userType,
                socialUser.getId());
        if (socialUserBind == null) {
            throw exception(AUTH_THIRD_LOGIN_NOT_BIND);
        }
        return socialUserBind.getUserId();
    }

    /**
     * 请求社交平台，获得授权的用户
     *
     * @param type 社交平台的类型
     * @param code 授权码
     * @param state 授权 state
     * @return 授权的用户
     */
/*     private AuthUser getAuthUser(Integer type, String code, String state) {
        AuthRequest authRequest = yudaoAuthRequestFactory.get(SocialTypeEnum.valueOfType(type).getSource());
        AuthCallback authCallback = AuthCallback.builder().code(code).state(state).build();
        AuthResponse<?> authResponse = authRequest.login(authCallback);
        log.info("[getAuthUser][请求社交平台 type({}) request({}) response({})]", type,
                toJsonString(authCallback), toJsonString(authResponse));
        if (!authResponse.ok()) {
            throw exception(SOCIAL_USER_AUTH_FAILURE, authResponse.getMsg());
        }
        return (AuthUser) authResponse.getData();
    } */

}
