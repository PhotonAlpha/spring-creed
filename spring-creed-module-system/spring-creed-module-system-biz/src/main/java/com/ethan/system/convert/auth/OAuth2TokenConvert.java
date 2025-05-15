package com.ethan.system.convert.auth;

import com.ethan.common.converter.BasicConvert;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenRespVO;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2Authorization;
import com.ethan.system.dal.entity.oauth2.client.CreedOAuth2AuthorizedClient;
import com.ethan.system.dal.entity.oauth2.graph.CreedOAuth2AuthorizationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OAuth2TokenConvert extends BasicConvert {

    OAuth2TokenConvert INSTANCE = Mappers.getMapper(OAuth2TokenConvert.class);

    // OAuth2AccessTokenCheckRespVO convert(CreedOAuth2AuthorizedClient bean);
    PageResult<OAuth2AccessTokenRespVO> convert0(PageResult<CreedOAuth2AuthorizationVO> page);
    // PageResult<OAuth2AccessTokenRespVO> convert(PageResult<CreedOAuth2AuthorizedClient> page);

    @Mapping(source = "accessTokenValue", target = "accessToken")
    @Mapping(source = "refreshTokenValue", target = "refreshToken")
    @Mapping(source = "registeredClients.clientId", target = "clientId")
    @Mapping(source = "principalName", target = "userName")
    @Mapping(source = "accessTokenIssuedAt", target = "createTime")
    @Mapping(source = "accessTokenExpiresAt", target = "expiresTime")
    OAuth2AccessTokenRespVO convert2(CreedOAuth2AuthorizationVO bean);

}
