package com.ethan.system.convert.oauth2;

import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientPageReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 14/2/25
 */
@Mapper
public interface OAuth2RegisteredClientConvert {
    OAuth2RegisteredClientConvert INSTANCE = Mappers.getMapper(OAuth2RegisteredClientConvert.class);

    @Mapping(source =".", target = "name")
    OAuth2ClientPageReqVO convert(String clientId);
}
