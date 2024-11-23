/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.security.oauth2.entity.client;


import com.ethan.common.converter.SetTypeConverter;
import com.ethan.common.pojo.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Set;

@Table(name = "creed_oauth2_client_configuration")
@Entity
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@Deprecated(forRemoval = true)
public class CreedOAuth2ClientConfiguration extends BaseDO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String registrationId;

    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String userInfoAuthenticationMethod;
    private String userNameAttributeName;
    private String jwkSetUri;
    private String issuerUri;
    private String configurationMetadata;

    private String clientId;
    private String clientSecret;

    @Column(name = "client_authentication_method")
    private String clientAuthenticationMethod;

    @Column(name = "authorization_grant_type")
    private String authorizationGrantType;

    private String redirectUri;
    @Convert(converter = SetTypeConverter.class)
    private Set<String> scopes;

    private String clientName;
}
