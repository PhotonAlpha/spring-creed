package com.ethan.system.dal.entity.oauth2;

import com.ethan.common.converter.ListTypeConverter;
import com.ethan.common.converter.SetTypeConverter;
import com.ethan.common.pojo.BaseXDO;
import com.ethan.system.convert.oauth2.OAuth2RegisteredClientConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "creed_oauth2_registered_client")
@Data
@EqualsAndHashCode
public class CreedOAuth2RegisteredClient extends BaseXDO {

    @Id
    @Column
    private String id;
    private String clientId;
    private Instant clientIdIssuedAt;
    private String clientSecret;
    private Instant clientSecretExpiresAt;
    private String clientName;
    /**
     * 授权方法：{@link  org.springframework.security.oauth2.core.ClientAuthenticationMethod}
     */
    @Column(length = 1000)
    @Convert(converter = ListTypeConverter.class)
    private List<String> clientAuthenticationMethods;

    /**
     * 授权类型：{@link  org.springframework.security.oauth2.core.AuthorizationGrantType}
     */
    @Convert(converter = ListTypeConverter.class)
    @Column(length = 1000)
    private List<String> authorizationGrantTypes;

    @Convert(converter = SetTypeConverter.class)
    @Column(length = 1000)
    private Set<String> redirectUris;

    @Convert(converter = SetTypeConverter.class)
    @Column(name = "post_logout_redirect_uris", length = 1000)
    private Set<String> postLogoutRedirectUris;

    @Convert(converter = SetTypeConverter.class)
    @Column(length = 1000)
    private Set<String> scopes;

    @Column(length = 2000)
    @Convert(converter = OAuth2RegisteredClientConverter.class)
    private Map<String, Object> clientSettings;

    @Column(length = 2000)
    @Convert(converter = OAuth2RegisteredClientConverter.class)
    private Map<String, Object> tokenSettings;

}
