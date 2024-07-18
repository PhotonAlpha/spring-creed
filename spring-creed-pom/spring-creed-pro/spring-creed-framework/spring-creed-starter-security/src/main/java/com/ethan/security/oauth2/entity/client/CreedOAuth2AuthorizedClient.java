/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.security.oauth2.entity.client;


import com.ethan.common.converter.SetTypeConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.Set;

@Table(name = "creed_oauth2_authorized_client")
@Entity
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@Deprecated(forRemoval = true)
public class CreedOAuth2AuthorizedClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private Integer userType;
    private String clientRegistrationId;

    private String principalName;
    private String accessTokenType;
    private String accessTokenValue;
    private Instant accessTokenIssuedAt;
    private Instant accessTokenExpiresAt;
    @Convert(converter = SetTypeConverter.class)
    private Set<String> accessTokenScopes;
    private String refreshTokenValue;
    private Instant refreshTokenIssuedAt;
    private Instant refreshTokenExpiresAt;
    private Instant createdAt;
}
