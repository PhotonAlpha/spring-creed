/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.security.oauth2.entity;


import com.ethan.common.constant.UserTypeEnum;
import com.ethan.common.converter.SetTypeConverter;
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

import java.time.Instant;
import java.util.Set;

@Table(name = "creed_oauth2_authorized_client")
@Entity
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@Deprecated
public class CreedOAuth2AuthorizedClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;

    private String clientRegistrationId;
    private String principalName;

    @Column
    private String accessTokenType;

    @Column(length = 4000)
    private String accessTokenValue;

    private Instant accessTokenIssuedAt;
    private Instant accessTokenExpiresAt;
    @Column
    @Convert(converter = SetTypeConverter.class)
    private Set<String> accessTokenScopes;

    @Column(length = 4000)
    private String refreshTokenValue;

    private Instant refreshTokenIssuedAt;
    private Instant refreshTokenExpiresAt;

    private Instant createdAt;
}
