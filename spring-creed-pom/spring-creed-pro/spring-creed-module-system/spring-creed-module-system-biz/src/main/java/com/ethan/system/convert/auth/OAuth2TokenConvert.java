package com.ethan.system.convert.auth;

import com.ethan.common.pojo.PageResult;
import com.ethan.security.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import com.ethan.security.oauth2.dto.OAuth2AccessTokenRespDTO;
import com.ethan.security.oauth2.entity.client.CreedOAuth2AuthorizedClient;
import com.ethan.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OAuth2TokenConvert {

    OAuth2TokenConvert INSTANCE = Mappers.getMapper(OAuth2TokenConvert.class);

    OAuth2AccessTokenCheckRespDTO convert(CreedOAuth2AuthorizedClient bean);

    PageResult<OAuth2AccessTokenRespVO> convert(PageResult<CreedOAuth2AuthorizedClient> page);

    OAuth2AccessTokenRespDTO convert2(CreedOAuth2AuthorizedClient bean);

}
