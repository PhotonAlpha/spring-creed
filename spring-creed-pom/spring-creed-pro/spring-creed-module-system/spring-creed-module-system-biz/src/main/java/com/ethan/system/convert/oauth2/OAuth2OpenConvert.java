package com.ethan.system.convert.oauth2;

import com.ethan.common.constant.UserTypeEnum;
import com.ethan.security.utils.SecurityFrameworkUtils;
import com.ethan.system.api.utils.oauth2.OAuth2Utils;
import com.ethan.system.controller.admin.oauth2.vo.open.OAuth2OpenAccessTokenRespVO;
import com.ethan.system.controller.admin.oauth2.vo.open.OAuth2OpenCheckTokenRespVO;
import com.ethan.system.dal.entity.oauth2.client.CreedOAuth2AuthorizedClient;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OAuth2OpenConvert {

    OAuth2OpenConvert INSTANCE = Mappers.getMapper(OAuth2OpenConvert.class);

    default OAuth2OpenAccessTokenRespVO convert(CreedOAuth2AuthorizedClient bean) {
        OAuth2OpenAccessTokenRespVO respVO = convert0(bean);
        respVO.setTokenType(SecurityFrameworkUtils.AUTHORIZATION_BEARER.toLowerCase());
        respVO.setExpiresIn(OAuth2Utils.getExpiresIn(bean.getAccessTokenExpiresAt()));
        respVO.setScope(OAuth2Utils.buildScopeStr(bean.getAccessTokenScopes()));
        return respVO;
    }
    OAuth2OpenAccessTokenRespVO convert0(CreedOAuth2AuthorizedClient bean);

    default OAuth2OpenCheckTokenRespVO convert2(CreedOAuth2AuthorizedClient bean) {
        OAuth2OpenCheckTokenRespVO respVO = convert3(bean);
        respVO.setExp(bean.getAccessTokenExpiresAt().toEpochMilli() / 1000L);
        respVO.setUserType(UserTypeEnum.ADMIN.getValue());
        return respVO;
    }
    OAuth2OpenCheckTokenRespVO convert3(CreedOAuth2AuthorizedClient bean);

    /* default OAuth2OpenAuthorizeInfoRespVO convert(CreedOAuth2AuthorizedClient client, List<CreedOAuth2RegisteredClient> approves) {
        // 构建 scopes
        List<Pair<String, Boolean>> scopes = new ArrayList<>(client.getScopes().size());
        Map<String, OAuth2ApproveDO> approveMap = CollUtils.convertMap(approves, OAuth2ApproveDO::getScope);
        client.getScopes().forEach(scope -> {
            OAuth2ApproveDO approve = approveMap.get(scope);
            scopes.add(Pair.of(scope, approve != null ? approve.getApproved() : false));
        });
        // 拼接返回
        return new OAuth2OpenAuthorizeInfoRespVO(
                new OAuth2OpenAuthorizeInfoRespVO.Client(client.getName(), client.getLogo()), scopes);
    } */

}
