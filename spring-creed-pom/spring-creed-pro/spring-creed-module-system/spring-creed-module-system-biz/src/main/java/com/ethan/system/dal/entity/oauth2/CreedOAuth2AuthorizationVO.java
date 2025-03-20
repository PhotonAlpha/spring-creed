package com.ethan.system.dal.entity.oauth2;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@AllArgsConstructor
@Data
public class CreedOAuth2AuthorizationVO {
    String registeredClientId;
    String principalName;
    String accessTokenValue;
    Instant accessTokenIssuedAt;
    String refreshTokenValue;
    Instant refreshTokenIssuedAt;
    String clientId;


}
