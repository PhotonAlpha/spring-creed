package com.ethan.system.dal.entity.oauth2;

import com.google.common.base.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "creed_oauth2_authorization")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CreedOAuth2Authorization {
    @Id
    @Column
    private String id;
    @Column(name = "registered_client_id") // 解决与CreedOAuth2AuthorizationVO的冲突问题
    private String registeredClientId;

    @Transient
    private String clientId;

    private String principalName;
    private String authorizationGrantType;
    @Column(length = 1000)
    private String authorizedScopes;

    @Lob @Basic(fetch=LAZY)
    @Column
    @ToString.Exclude
    private byte[] attributes;

    @Column(length = 500)
    private String state;

    @Column(length = 4000)
    private String authorizationCodeValue;
    private Instant authorizationCodeIssuedAt;
    private Instant authorizationCodeExpiresAt;

    @Lob @Basic(fetch=LAZY)
    @Column
    @ToString.Exclude
    private byte[] authorizationCodeMetadata;

    @Column(length = 4000)
    private String accessTokenValue;

    private Instant accessTokenIssuedAt;
    private Instant accessTokenExpiresAt;

    @Lob @Basic(fetch=LAZY)
    @Column
    @ToString.Exclude
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
    @ToString.Exclude
    private byte[] refreshTokenMetadata;

    @Column(length = 4000)
    private String oidcIdTokenValue;

    private Instant oidcIdTokenIssuedAt;
    private Instant oidcIdTokenExpiresAt;

    @Lob @Basic(fetch=LAZY)
    @Column
    @ToString.Exclude
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreedOAuth2Authorization that = (CreedOAuth2Authorization) o;
        return version == that.version && Objects.equal(id, that.id) && Objects.equal(registeredClientId, that.registeredClientId) && Objects.equal(clientId, that.clientId) && Objects.equal(principalName, that.principalName) && Objects.equal(authorizationGrantType, that.authorizationGrantType) && Objects.equal(authorizedScopes, that.authorizedScopes) && Objects.equal(attributes, that.attributes) && Objects.equal(state, that.state) && Objects.equal(authorizationCodeValue, that.authorizationCodeValue) && Objects.equal(authorizationCodeIssuedAt, that.authorizationCodeIssuedAt) && Objects.equal(authorizationCodeExpiresAt, that.authorizationCodeExpiresAt) && Objects.equal(authorizationCodeMetadata, that.authorizationCodeMetadata) && Objects.equal(accessTokenValue, that.accessTokenValue) && Objects.equal(accessTokenIssuedAt, that.accessTokenIssuedAt) && Objects.equal(accessTokenExpiresAt, that.accessTokenExpiresAt) && Objects.equal(accessTokenMetadata, that.accessTokenMetadata) && Objects.equal(accessTokenType, that.accessTokenType) && Objects.equal(accessTokenScopes, that.accessTokenScopes) && Objects.equal(refreshTokenValue, that.refreshTokenValue) && Objects.equal(refreshTokenIssuedAt, that.refreshTokenIssuedAt) && Objects.equal(refreshTokenExpiresAt, that.refreshTokenExpiresAt) && Objects.equal(refreshTokenMetadata, that.refreshTokenMetadata) && Objects.equal(oidcIdTokenValue, that.oidcIdTokenValue) && Objects.equal(oidcIdTokenIssuedAt, that.oidcIdTokenIssuedAt) && Objects.equal(oidcIdTokenExpiresAt, that.oidcIdTokenExpiresAt) && Objects.equal(oidcIdTokenMetadata, that.oidcIdTokenMetadata) && Objects.equal(oidcIdTokenClaims, that.oidcIdTokenClaims) && Objects.equal(userCodeValue, that.userCodeValue) && Objects.equal(userCodeIssuedAt, that.userCodeIssuedAt) && Objects.equal(userCodeExpiresAt, that.userCodeExpiresAt) && Objects.equal(userCodeMetadata, that.userCodeMetadata) && Objects.equal(deviceCodeValue, that.deviceCodeValue) && Objects.equal(deviceCodeIssuedAt, that.deviceCodeIssuedAt) && Objects.equal(deviceCodeExpiresAt, that.deviceCodeExpiresAt) && Objects.equal(deviceCodeMetadata, that.deviceCodeMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, registeredClientId, clientId, principalName, authorizationGrantType, authorizedScopes, attributes, state, authorizationCodeValue, authorizationCodeIssuedAt, authorizationCodeExpiresAt, authorizationCodeMetadata, accessTokenValue, accessTokenIssuedAt, accessTokenExpiresAt, accessTokenMetadata, accessTokenType, accessTokenScopes, refreshTokenValue, refreshTokenIssuedAt, refreshTokenExpiresAt, refreshTokenMetadata, oidcIdTokenValue, oidcIdTokenIssuedAt, oidcIdTokenExpiresAt, oidcIdTokenMetadata, oidcIdTokenClaims, userCodeValue, userCodeIssuedAt, userCodeExpiresAt, userCodeMetadata, deviceCodeValue, deviceCodeIssuedAt, deviceCodeExpiresAt, deviceCodeMetadata, version);
    }
}
