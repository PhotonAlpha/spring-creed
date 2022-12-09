package com.ethan.security.consumer.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "creed_oauth2_authorization")
@Data
@EqualsAndHashCode
public class CreedAuthority {
    @Id
    @Column
    private String id;
    private String registeredClientId;
    private String principalName;
    private String authorizationGrantType;
    @Column(length = 1000)
    private String authorizedScopes;

    @Lob @Basic(fetch=LAZY)
    @Column
    private String attributes;

    @Column(length = 500)
    private String state;

    @Lob @Basic(fetch=LAZY)
    @Column
    private String authorizationCodeValue;
    private Instant authorizationCodeIssuedAt;
    private Instant authorizationCodeExpiresAt;

    @Lob @Basic(fetch=LAZY)
    @Column
    private String authorizationCodeMetadata;

    @Lob @Basic(fetch=LAZY)
    @Column
    private String accessTokenValue;

    private Instant accessTokenIssuedAt;
    private Instant accessTokenExpiresAt;

    @Lob @Basic(fetch=LAZY)
    @Column
    private String accessTokenMetadata;

    private String accessTokenType;

    @Column(length = 1000)
    private String accessTokenScopes;

    @Lob @Basic(fetch=LAZY)
    @Column
    private String refreshTokenValue;
    private Instant refreshTokenIssuedAt;
    private Instant refreshTokenExpiresAt;

    @Lob @Basic(fetch=LAZY)
    @Column
    private String refreshTokenMetadata;

    @Lob @Basic(fetch=LAZY)
    @Column
    private String oidcIdTokenValue;

    private Instant oidcIdTokenIssuedAt;
    private Instant oidcIdTokenExpiresAt;

    @Lob @Basic(fetch=LAZY)
    @Column
    private String oidcIdTokenMetadata;

    @Lob @Basic(fetch=LAZY)
    @Column
    private String oidcIdTokenClaims;
}
