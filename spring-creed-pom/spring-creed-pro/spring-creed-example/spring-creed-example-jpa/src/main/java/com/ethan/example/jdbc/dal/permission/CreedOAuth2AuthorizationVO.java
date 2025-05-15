package com.ethan.example.jdbc.dal.permission;

import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "creed_oauth2_authorization")
public class CreedOAuth2AuthorizationVO {
    private String id;
    private String registeredClientId;
    private String principalName;
    private String accessTokenValue;
    private Instant accessTokenIssuedAt;
    private String refreshTokenValue;
    private Instant refreshTokenIssuedAt;
    @Transient
    private String clientId;
    // @OneToMany(mappedBy = "auth2Authorizations")
    // private CreedOauth2RegisteredClientVO registeredClients;

}
