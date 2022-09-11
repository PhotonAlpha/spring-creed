package com.ethan.std.provider;

import com.ethan.std.utils.EncryptUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/8/2022 11:35 AM
 *
 * as {@link CustomizeTokenServices} implementation, the bean no need . Use for {@link DefaultTokenServices}
 */
@Deprecated
public class CustomizeTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String value = accessToken.getValue();
        ((DefaultOAuth2AccessToken)accessToken).setValue(EncryptUtils.enhanceTokenKey(value));
        return accessToken;
    }
}
