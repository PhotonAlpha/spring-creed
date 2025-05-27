package com.ethan.example.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.encrypt.RsaSecretEncryptor;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.text.ParseException;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 22/5/25
 */
public class SecurityConfigTest {
    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void test_RsaSecretEncryptor() throws JOSEException, IOException, ParseException {

        JWKSet jwkSet = JWKSet.parse(IOUtils.toString(new ClassPathResource("vault/jwks.json").getInputStream(), StandardCharsets.UTF_8));
        JWK jwk = jwkSet.getKeyByKeyId("nginx-creed-mall");
        RSAKey rsaKey = jwk.toRSAKey();
        TextEncryptor textEncryptor = new RsaSecretEncryptor(new KeyPair(rsaKey.toPublicKey(), rsaKey.toPrivateKey()));
        System.out.println(textEncryptor.encrypt("changeit"));
    }
}
