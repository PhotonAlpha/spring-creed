/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.security.oauth2.dto;

import com.ethan.common.constant.UserTypeEnum;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

@Deprecated(forRemoval = true)
public class CreedOAuth2AuthorizedClientDTO extends OAuth2AuthorizedClient {


    public CreedOAuth2AuthorizedClientDTO(ClientRegistration clientRegistration, String principalName, OAuth2AccessToken accessToken) {
        super(clientRegistration, principalName, accessToken);
    }

    public CreedOAuth2AuthorizedClientDTO(ClientRegistration clientRegistration, String principalName, OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {
        super(clientRegistration, principalName, accessToken, refreshToken);
    }

    private Long id;
    private String userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;

    private String principalName;

    // private String clientRegistrationId;
    private ClientRegistration clientRegistration;

    /*     @Column
        private String accessTokenType;

        @Lob
        @Basic(fetch=LAZY)
        @Column
        private String accessTokenValue;

        private Instant accessTokenIssuedAt;
        private Instant accessTokenExpiresAt;
        @Column
        @Convert(converter = SetTypeConverter.class)
        private Set<String> accessTokenScopes; */
    private OAuth2AccessToken accessToken;

    /*     private String refreshTokenValue;

        private Instant refreshTokenIssuedAt;
        private Instant refreshTokenExpiresAt; */
    private OAuth2RefreshToken refreshToken;
}
