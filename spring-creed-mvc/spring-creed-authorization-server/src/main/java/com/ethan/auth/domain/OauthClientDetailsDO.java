package com.ethan.auth.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * OAuth2 Client
 */
@Getter
@Setter
@Table(name = "oauth_client_details")
@Entity
public class OauthClientDetailsDO {
  @Id
  @Column(name = "client_id", length = 256)
  private String clientId;

  @Column(name = "resource_ids", length = 256)
  private String resourceIds;

  @Column(name = "client_secret", length = 256)
  private String clientSecret;

  @Column(name = "scope", length = 256)
  private String scope;

  @Column(name = "authorized_grant_types", length = 256)
  private String authorizedGrantTypes;

  @Column(name = "web_server_redirect_uri", length = 500)
  private String webServerRedirectUri;

  @Column(name = "authorities", length = 256)
  private String authorities;

  @Column(name = "access_token_validity")
  private Integer access_token_validity;

  @Column(name = "refresh_token_validity")
  private Integer refresh_token_validity;

  @Column(name = "additional_information", length = 4096)
  private String additional_information;

  @Column(name = "autoapprove", length = 256)
  private String autoApprove;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    OauthClientDetailsDO that = (OauthClientDetailsDO) o;

    return new EqualsBuilder()
        .append(clientId, that.clientId)
        .append(resourceIds, that.resourceIds)
        .append(clientSecret, that.clientSecret)
        .append(scope, that.scope)
        .append(authorizedGrantTypes, that.authorizedGrantTypes)
        .append(webServerRedirectUri, that.webServerRedirectUri)
        .append(authorities, that.authorities)
        .append(access_token_validity, that.access_token_validity)
        .append(refresh_token_validity, that.refresh_token_validity)
        .append(autoApprove, that.autoApprove)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(clientId)
        .append(resourceIds)
        .append(clientSecret)
        .append(scope)
        .append(authorizedGrantTypes)
        .append(webServerRedirectUri)
        .append(authorities)
        .append(access_token_validity)
        .append(refresh_token_validity)
        .append(autoApprove)
        .toHashCode();
  }

  @Override
  public String toString() {
    return "OauthClientDetailsDO{" +
        "clientId='" + clientId + '\'' +
        ", resourceIds='" + resourceIds + '\'' +
        ", clientSecret='" + clientSecret + '\'' +
        ", scope='" + scope + '\'' +
        ", authorizedGrantTypes='" + authorizedGrantTypes + '\'' +
        ", webServerRedirectUri='" + webServerRedirectUri + '\'' +
        ", authorities='" + authorities + '\'' +
        ", access_token_validity=" + access_token_validity +
        ", refresh_token_validity=" + refresh_token_validity +
        ", autoApprove='" + autoApprove + '\'' +
        '}';
  }
}
