package com.ethan.system.config.oauth2;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
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

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    /**
     * {@link org.springframework.security.oauth2.server.authorization.web.NimbusJwkSetEndpointFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}
     * 根据 JWKs json 字符串，创建用于验证jwt的JWKs对象
     */
    @Test
    void testJWKSource() {
        JWKSource<SecurityContext> keySource = systemAuthorizationServerConfig.jwkSource();
        JWKSelector jwkSelector = new JWKSelector(new JWKMatcher.Builder().build());
        JWKSet jwkSet;
        try {
            jwkSet = new JWKSet(keySource.get(jwkSelector, null));
            System.out.println(jwkSet.toString(false));
        }
        catch (Exception ex) {
            throw new IllegalStateException("Failed to select the JWK(s) -> " + ex.getMessage(), ex);
        }
    }

    @Test
    void generateJwt() {
        Instant issuedAt = Instant.now();
        Instant expiresAt;
        JwsAlgorithm jwsAlgorithm = SignatureAlgorithm.RS256;
        // TODO Allow configuration for ID Token time-to-live
        expiresAt = issuedAt.plus(30, ChronoUnit.HOURS);

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

    /**
     * 私钥生成 token ， 公钥验证token
     * @throws ParseException
     * @throws JOSEException
     */
    @Test
    void jwkParseTest()throws ParseException, JOSEException {
        JWKSet jwkSet = JWKSet.parse("""
{
    "keys": [
        {
            "kty": "RSA",
            "e": "AQAB",
            "kid": "root-creed-mall",
            "n": "v7y1eDeEKhXhuBdKSWXoqFIMOJuWs1MHBWlHA0wj6Iqo3vQ_ttqbhy1-_5zR24s87fcEFuwzhyjjGG01Gu48b0Lo_Z5YWScGfKL8WbK68Lli1CHlWH6WOWl1v6_QTHitMzWqXH5zuNY7yJnaTtTje7lWJNrFZc7fzUuvXwsEAJs5ri9uzQX6cvOGATlsdrRfo6PNg0HUZj9eJ-IpUEnSe3N0ON1NR4Z9kWy4TEARTFJY8HdQPa4LajdvghYnCg8kzRwePBKVZkKsGpUhAHWiTunmuDwSpvocwXLrzC4gUoTmQgeZoJadYkmowXO36M6YXIfeh8arHgEVobvS7dJSbw"
        },
        {
            "kty": "RSA",
            "e": "AQAB",
            "kid": "nginx-creed-mall",
            "n": "nfJ14sTRFFqFlAs_W-bTEL7swW7rLRtgpb7SMyrdE_geq-S8JN70NMXv1wy7Mb6aYgAG0Y1_eKwx62ce-F8YRvpYJ2bUtD7f7C-jSf4noT4ZlnQG3_F-hVq4kMPgBiEzVxVzh6M721QdDZ4XqB-_DuJelSpkMlRvSxHNUPz1f1XznhzSwkEmhzRHTPV3WkbAL4DpAIU-T16EltTspX7D-21pLlYGEmqAj-J7SwE3pcQFx_lvudGauKlGg0Piax0kGvTlaBHmLmdXDk7ye97Sq0P9fFb40FprL6ZiDlBtbAEtgidQJfbMdkWmM6TnBYSg3agiG5wgNpsDSxgaPl4mUw"
        }
    ]
}
""");
        System.out.println(jwkSet.getKeys());

        String token = "eyJraWQiOiJyb290LWNyZWVkLW1hbGwiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJldGhhbiIsImF1ZCI6Imp3dC1jbGllbnQiLCJuYmYiOjE3NDc5MDE5NDcsInNjb3BlIjpbIm1lc3NhZ2UucmVhZCIsIm1lc3NhZ2Uud3JpdGUiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo0ODA4MCIsImV4cCI6MTc0ODAwOTk0NywiaWF0IjoxNzQ3OTAxOTQ3LCJ1dWlkIjoiNDYzYjA0OTctMzVlOC00OTllLWJlOWQtYWYyODIwMzliZGM4IiwianRpIjoiOTI2MTYxOWYtZTY3NC00N2E3LWE5ZGYtNTVkN2RmZGMwMjA5In0.MKPTt3YW9ewB9HVixr6WtYKGNNyEBCkI3Tn8WCjl8h8aL44JAbygauncORSr1dxApsAXGLK9qeQ70BR3300hehNeCAppVP40IHoQ9IHYewjVXI8cEsdAZx3ZojB8y0_oxV0hjsXhrewhj7aYAnBgyzgGDXA2lQhdrWxXsR5S0-PFTaLsJKEpMA31WdhW8A-mHiaalAk1MPVAfq7lY8AGX5p870w5awg2ubnj40ppFPeqqF5AuH3_ozpSoJjdmWG35w3hzGVRsToxG5KSR_cG1v3V_5UPmXcrVYtohBzym1okkwOyPOY8vqVVO3zOwzMSedyyGDNkTRxI1G8DI5txUA";
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSHeader header = signedJWT.getHeader();
        String keyID = header.getKeyID();System.out.println("getKeyID:"+ keyID);
        JWK jwk = jwkSet.getKeyByKeyId(keyID);

        RSAKey rsaKey = jwk.toRSAKey();
        PublicKey publicKey = rsaKey.toPublicKey();
        boolean verified = signedJWT.verify(new RSASSAVerifier((RSAPublicKey)publicKey));
        if (verified) {
            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
            for (Map.Entry<String,Object> entry : jwtClaimsSet.getClaims().entrySet()) {
                log.info("key:{} value:{}", entry.getKey(), entry.getValue());
            }
        } else{
            log.error("verified:{}", verified);
        }


    }
}
