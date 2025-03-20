package com.ethan.system.dal.entity.oauth2;

import com.ethan.system.dal.entity.permission.SystemGroups;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "creed_oauth2_authorization")
@Data
@EqualsAndHashCode(exclude = "registeredClient")
public class CreedOAuth2Authorization {
    @Id
    @Column
    private String id;
    private String registeredClientId;

    @Transient
    private String clientId;

    private String principalName;
    private String authorizationGrantType;
    @Column(length = 1000)
    private String authorizedScopes;

    @Lob @Basic(fetch=LAZY)
    @Column
    private byte[] attributes;

    @Column(length = 500)
    private String state;

    @Column(length = 4000)
    private String authorizationCodeValue;
    private Instant authorizationCodeIssuedAt;
    private Instant authorizationCodeExpiresAt;

    @Lob @Basic(fetch=LAZY)
    @Column
    private byte[] authorizationCodeMetadata;

    @Column(length = 4000)
    private String accessTokenValue;

    private Instant accessTokenIssuedAt;
    private Instant accessTokenExpiresAt;

    @Lob @Basic(fetch=LAZY)
    @Column
    private byte[] accessTokenMetadata;

    private String accessTokenType;

    @Column(length = 1000)
    private String accessTokenScopes;

    @Column(length = 4000)
    private String refreshTokenValue;
    private Instant refreshTokenIssuedAt;
    private Instant refreshTokenExpiresAt;

    @Lob @Basic(fetch=LAZY)
    @Column
    private byte[] refreshTokenMetadata;

    @Column(length = 4000)
    private String oidcIdTokenValue;

    private Instant oidcIdTokenIssuedAt;
    private Instant oidcIdTokenExpiresAt;

    @Lob @Basic(fetch=LAZY)
    @Column
    private byte[] oidcIdTokenMetadata;

    @Column(length = 2000)
    private String oidcIdTokenClaims;

    @Column(length = 4000)
    private String userCodeValue;

    private Instant userCodeIssuedAt;
    private Instant userCodeExpiresAt;
    @Column(length = 2000)
    private String userCodeMetadata;

    @Column(length = 4000)
    private String deviceCodeValue;

    private Instant deviceCodeIssuedAt;
    private Instant deviceCodeExpiresAt;
    @Column(length = 2000)
    private String deviceCodeMetadata;


    @Version
    private int version;
}
