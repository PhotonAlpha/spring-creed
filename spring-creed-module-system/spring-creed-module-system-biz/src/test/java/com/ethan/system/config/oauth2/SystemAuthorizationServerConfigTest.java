package com.ethan.system.config.oauth2;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 9/12/24
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class SystemAuthorizationServerConfigTest {
    @InjectMocks
    SystemAuthorizationServerConfig systemAuthorizationServerConfig;

    @Test
    void generateJwt() {
        Instant issuedAt = Instant.now();
        Instant expiresAt;
        JwsAlgorithm jwsAlgorithm = SignatureAlgorithm.RS256;
            // TODO Allow configuration for ID Token time-to-live
        expiresAt = issuedAt.plus( 30, ChronoUnit.HOURS);

        // @formatter:off
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();
        claimsBuilder.issuer("http://localhost:48080");
        claimsBuilder
                .subject("ethan")
                .audience(Collections.singletonList("jwt-client"))
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .id(UUID.randomUUID().toString());
        claimsBuilder.notBefore(issuedAt);
        claimsBuilder.claim(OAuth2ParameterNames.SCOPE, Arrays.asList("message.read","message.write"));
        claimsBuilder.claim("uuid", UUID.randomUUID().toString());

        JwsHeader.Builder jwsHeaderBuilder = JwsHeader.with(jwsAlgorithm)
                .keyId("root-creed-mall");

        JwsHeader jwsHeader = jwsHeaderBuilder.build();
        JwtClaimsSet claims = claimsBuilder.build();

        JWKSource<SecurityContext> keySource = systemAuthorizationServerConfig.jwkSource();
        NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(keySource);

        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims));
        System.out.println(jwt.getTokenValue());


        JwtDecoder jwtDecoder = jwtDecoder(keySource);
        Jwt jwtAssertion = null;
        try {
            jwtAssertion = jwtDecoder.decode(jwt.getTokenValue());
        }
        catch (JwtException ex) {
            ex.printStackTrace();
        }
        System.out.println(jwtAssertion.getClaims());
    }


    public static JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        Set<JWSAlgorithm> jwsAlgs = new HashSet<>();
        jwsAlgs.addAll(JWSAlgorithm.Family.RSA);
        jwsAlgs.addAll(JWSAlgorithm.Family.EC);
        jwsAlgs.addAll(JWSAlgorithm.Family.HMAC_SHA);
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        JWSKeySelector<SecurityContext> jwsKeySelector = new JWSVerificationKeySelector<>(jwsAlgs, jwkSource);
        jwtProcessor.setJWSKeySelector(jwsKeySelector);
        // Override the default Nimbus claims set verifier as NimbusJwtDecoder handles it
        // instead
        jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
        });
        return new NimbusJwtDecoder(jwtProcessor);
    }

}
