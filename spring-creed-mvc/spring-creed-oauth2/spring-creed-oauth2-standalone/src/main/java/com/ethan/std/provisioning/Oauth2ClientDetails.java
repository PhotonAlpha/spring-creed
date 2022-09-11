package com.ethan.std.provisioning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ethan.std.utils.InstanceUtil.MAPPER;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/2/2022 5:12 PM
 */

@Data
@Entity
@Table(name = "oauth_client_details")
public class Oauth2ClientDetails implements ClientDetails {

    private static final long serialVersionUID = 2403866887677795512L;
    @Id
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "resource_ids")
    private String resourceIds;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "scope")
    private String scope;

    @Column(name = "authorized_grant_types")
    private String authorizedGrantTypes;

    @Column(name = "web_server_redirect_uri")
    private String webServerRedirectUri;

    @Column(name = "authorities")
    private String authorities;

    @Column(name = "access_token_validity")
    private Integer accessTokenValidity;

    @Column(name = "refresh_token_validity")
    private Integer refreshTokenValidity;

    @Column(name = "additional_information")
    private String additionalInformation;

    @Column(name = "autoapprove")
    private String autoApprove;






    @Override
    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }

    @Override
    public boolean isScoped() {
        return this.scope != null && !this.scope.isEmpty();
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return Optional.ofNullable(this.webServerRedirectUri)
                .map(s -> s.replaceAll("\\s+", ","))
                .map(org.springframework.util.StringUtils::commaDelimitedListToStringArray)
                .map(Stream::of).orElse(Stream.empty())
                .collect(Collectors.toSet());
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return this.accessTokenValidity;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return this.refreshTokenValidity;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        Set<String> autoApproveScopes = Optional.ofNullable(this.autoApprove)
                .map(s -> s.replaceAll("\\s+", ","))
                .map(org.springframework.util.StringUtils::commaDelimitedListToStringArray)
                .map(Stream::of).orElse(Stream.empty())
                .collect(Collectors.toSet());

        if (autoApproveScopes == null || autoApproveScopes.isEmpty()) {
            return false;
        }
        for (String auto : autoApproveScopes) {
            if (auto.equals("true") || scope.matches(auto)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        try {
            return MAPPER.readValue(this.additionalInformation, Map.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setAdditionalInformation(Map<String, Object> additionalInformation) {
        try {
            this.additionalInformation = MAPPER.writeValueAsString(additionalInformation);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return Optional.ofNullable(this.authorities)
                .map(s -> s.replaceAll("\\s+", ","))
                .map(org.springframework.util.StringUtils::commaDelimitedListToStringArray)
                .map(Stream::of).orElse(Stream.empty())
                .map(s -> (GrantedAuthority) () -> s)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return Optional.ofNullable(this.authorizedGrantTypes)
                .map(s -> s.replaceAll("\\s+", ","))
                .map(org.springframework.util.StringUtils::commaDelimitedListToStringArray)
                .map(Stream::of).orElse(Stream.empty())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getScope() {
        return Optional.ofNullable(this.scope)
                .map(s -> s.replaceAll("\\s+", ","))
                .map(org.springframework.util.StringUtils::commaDelimitedListToStringArray)
                .map(Stream::of).orElse(Stream.empty())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getResourceIds() {
        return Optional.ofNullable(this.resourceIds)
                .map(s -> s.replaceAll("\\s+", ","))
                .map(org.springframework.util.StringUtils::commaDelimitedListToStringArray)
                .map(Stream::of).orElse(Stream.empty())
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "Oauth2ClientDetails{" +
                "clientId='" + clientId + '\'' +
                ", resourceIds='" + resourceIds + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", scope='" + scope + '\'' +
                ", authorizedGrantTypes='" + authorizedGrantTypes + '\'' +
                ", webServerRedirectUri='" + webServerRedirectUri + '\'' +
                ", authorities='" + authorities + '\'' +
                ", accessTokenValidity=" + accessTokenValidity +
                ", refreshTokenValidity=" + refreshTokenValidity +
                ", additionalInformation='" + additionalInformation + '\'' +
                ", autoApprove='" + autoApprove + '\'' +
                '}';
    }
}
