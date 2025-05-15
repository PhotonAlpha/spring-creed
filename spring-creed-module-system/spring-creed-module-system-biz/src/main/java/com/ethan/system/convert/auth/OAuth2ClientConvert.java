package com.ethan.system.convert.auth;

import com.ethan.common.converter.BasicConvert;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientRespVO;
import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientSaveReqVO;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2RegisteredClient;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * OAuth2 客户端 Convert
 *
 * 
 */
@Mapper
public interface OAuth2ClientConvert extends BasicConvert {

    OAuth2ClientConvert INSTANCE = Mappers.getMapper(OAuth2ClientConvert.class);

    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "secret", target = "clientSecret")
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "redirectUris", target = "redirectUris")
    @Mapping(source = "authenticationMethods", target = "clientAuthenticationMethods")
    @Mapping(source = "authorizedGrantTypes", target = "authorizationGrantTypes")
    @Mapping(source = "scopes", target = "scopes")
    CreedOAuth2RegisteredClient convert(OAuth2ClientSaveReqVO createReqVO);

    /**
     * postUpdate supplement
     */
    @AfterMapping
    default void postUpdate(OAuth2ClientSaveReqVO vo, @MappingTarget CreedOAuth2RegisteredClient tar) {
        TokenSettings.Builder tokenSettingsBuilder;
        if (Objects.nonNull(tar.getTokenSettings())) {
            tokenSettingsBuilder = TokenSettings.withSettings(tar.getTokenSettings());
        } else {
            tokenSettingsBuilder = TokenSettings.builder();
        }
        if (Objects.nonNull(vo.getAccessTokenValiditySeconds())) {
            tokenSettingsBuilder.accessTokenTimeToLive(Duration.ofSeconds(vo.getAccessTokenValiditySeconds()));
        }
        if (Objects.nonNull(vo.getRefreshTokenValiditySeconds())) {
            tokenSettingsBuilder.refreshTokenTimeToLive(Duration.ofSeconds(vo.getRefreshTokenValiditySeconds()));
        }
        tar.setTokenSettings(tokenSettingsBuilder.build().getSettings());

        ClientSettings.Builder clientSettingsBuilder;
        if (Objects.nonNull(tar.getClientSettings())) {
            clientSettingsBuilder = ClientSettings.withSettings(tar.getClientSettings());
        } else {
            clientSettingsBuilder = ClientSettings.builder();
            clientSettingsBuilder
                    .requireAuthorizationConsent(true);
        }

        tar.setClientSettings(clientSettingsBuilder.build().getSettings());

        tar.setClientIdIssuedAt(Instant.now());
        tar.setClientSecretExpiresAt(LocalDateTime.now().plusYears(5).atZone(ZoneId.systemDefault()).toInstant());
        if (CollectionUtils.isEmpty(tar.getScopes())) {
            tar.setScopes(Set.of(OidcScopes.OPENID, OidcScopes.PROFILE));
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(ignore = true, target = "clientSecret") //更新时，跳过密码更新
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "redirectUris", target = "redirectUris")
    @Mapping(source = "authorizedGrantTypes", target = "authorizationGrantTypes")
    void update(OAuth2ClientSaveReqVO vo, @MappingTarget CreedOAuth2RegisteredClient registeredClient);

    @Mapping(target = "clientId", source = "clientId")
    @Mapping(target = "secret", source = "clientSecret")
    @Mapping(target = "name", source = "clientName")
    @Mapping(target = "redirectUris", source = "redirectUris")
    @Mapping(target = "authorizedGrantTypes", source = "authorizationGrantTypes")
    @Mapping(target = "authenticationMethods", source = "clientAuthenticationMethods")
    @Mapping(target = "scopes", source = "scopes")
    OAuth2ClientRespVO convert(CreedOAuth2RegisteredClient client);

    @AfterMapping
    default void reversionUpdate(CreedOAuth2RegisteredClient tar, @MappingTarget OAuth2ClientRespVO vo) {
        Optional.ofNullable(tar.getTokenSettings())
                .map(TokenSettings::withSettings)
                .map(TokenSettings.Builder::build)
                .ifPresent(setting -> {
                    vo.setAccessTokenValiditySeconds(setting.getAccessTokenTimeToLive().toSeconds());
                    vo.setRefreshTokenValiditySeconds(setting.getRefreshTokenTimeToLive().toSeconds());
                });

    }

    List<OAuth2ClientRespVO> convert(List<CreedOAuth2RegisteredClient> client);
    PageResult<OAuth2ClientRespVO> convertPage(PageResult<CreedOAuth2RegisteredClient> pageResult);
}
