package com.ethan.system.convert.auth;

import com.ethan.common.utils.collection.CollUtils;
import com.ethan.security.oauth2.entity.CreedOAuth2AuthorizedClient;
import com.ethan.security.websecurity.entity.CreedAuthorities;
import com.ethan.security.websecurity.entity.CreedUser;
import com.ethan.system.constant.permission.MenuIdEnum;
import com.ethan.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.ethan.system.controller.admin.auth.vo.AuthMenuRespVO;
import com.ethan.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.ethan.system.controller.admin.auth.vo.AuthSmsLoginReqVO;
import com.ethan.system.controller.admin.auth.vo.AuthSmsSendReqVO;
import com.ethan.system.controller.admin.auth.vo.AuthSocialLoginReqVO;
import com.ethan.system.controller.admin.sms.dto.code.SmsCodeSendReqDTO;
import com.ethan.system.controller.admin.sms.dto.code.SmsCodeUseReqDTO;
import com.ethan.system.controller.admin.social.dto.SocialUserBindReqDTO;
import com.ethan.system.dal.entity.permission.MenuDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface AuthConvert {

    AuthConvert INSTANCE = Mappers.getMapper(AuthConvert.class);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "accessTokenValue", target = "accessToken")
    @Mapping(source = "refreshTokenValue", target = "refreshToken")
    @Mapping(source = "accessTokenExpiresAt", target = "expiresTime")
    AuthLoginRespVO convert(CreedOAuth2AuthorizedClient bean);

    default LocalDateTime fromInstant(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    default AuthPermissionInfoRespVO convert(CreedUser user, List<CreedAuthorities> roleList, List<MenuDO> menuList) {
        return AuthPermissionInfoRespVO.builder()
            .user(AuthPermissionInfoRespVO.UserVO.builder().id(user.getId()).nickname(user.getNickname()).avatar(user.getAvatar()).build())
            .roles(CollUtils.convertSet(roleList, CreedAuthorities::getAuthority))
            .permissions(CollUtils.convertSet(menuList, MenuDO::getPermission))
            .build();
    }

    AuthMenuRespVO convertTreeNode(MenuDO menu);

    /**
     * 将菜单列表，构建成菜单树
     *
     * @param menuList 菜单列表
     * @return 菜单树
     */
    default List<AuthMenuRespVO> buildMenuTree(List<MenuDO> menuList) {
        // 排序，保证菜单的有序性
        menuList.sort(Comparator.comparing(MenuDO::getSort));
        // 构建菜单树
        // 使用 LinkedHashMap 的原因，是为了排序 。实际也可以用 Stream API ，就是太丑了。
        Map<Long, AuthMenuRespVO> treeNodeMap = new LinkedHashMap<>();
        menuList.forEach(menu -> treeNodeMap.put(menu.getId(), AuthConvert.INSTANCE.convertTreeNode(menu)));
        // 处理父子关系
        treeNodeMap.values().stream().filter(node -> !node.getParentId().equals(MenuIdEnum.ROOT.getId())).forEach(childNode -> {
            // 获得父节点
            AuthMenuRespVO parentNode = treeNodeMap.get(childNode.getParentId());
            if (parentNode == null) {
                LoggerFactory.getLogger(getClass()).error("[buildRouterTree][resource({}) 找不到父资源({})]",
                    childNode.getId(), childNode.getParentId());
                return;
            }
            // 将自己添加到父节点中
            if (parentNode.getChildren() == null) {
                parentNode.setChildren(new ArrayList<>());
            }
            parentNode.getChildren().add(childNode);
        });
        // 获得到所有的根节点
        return CollUtils.filterList(treeNodeMap.values(), node -> MenuIdEnum.ROOT.getId().equals(node.getParentId()));
    }

    SocialUserBindReqDTO convert(Long userId, Integer userType, AuthSocialLoginReqVO reqVO);

    SmsCodeSendReqDTO convert(AuthSmsSendReqVO reqVO);

    SmsCodeUseReqDTO convert(AuthSmsLoginReqVO reqVO, Integer scene, String usedIp);

}
