package com.ethan.system.config.oauth2;

import com.ethan.common.exception.ServerException;
import com.ethan.system.dal.registration.JpaOAuth2AuthorizationConsentService;
import com.ethan.system.dal.registration.JpaOAuth2AuthorizationService;
import com.ethan.system.dal.registration.JpaRegisteredClientRepository;
import com.ethan.system.dal.repository.oauth2.CreedOAuth2AuthorizationConsentRepository;
import com.ethan.system.dal.repository.oauth2.CreedOAuth2AuthorizationRepository;
import com.ethan.system.dal.repository.oauth2.CreedOAuth2RegisteredClientRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 9/13/24
 *
 * 配置Oauth2 服务器认证相关配置
 */
@Configuration(proxyBeanMethods = false)
@Slf4j
public class SystemAuthorizationServerConfig {
    @Bean
    public RegisteredClientRepository registeredClientRepository(CreedOAuth2RegisteredClientRepository clientRepository) {
        return new JpaRegisteredClientRepository(clientRepository);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        try (InputStream in = new ClassPathResource("ssl/keystore.jks").getInputStream()) {
            KeyStore keyStore = loadKeyStore(in, "changeit");
            var keyId = "root-creed-mall";
            KeyPair keyPair = loadJksKeyPair(keyStore, "changeit", keyId);
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            // Base64 encoded string
            // String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            // String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            // log.debug("publicKey:{}", publicKeyString);
            // log.debug("privateKey:{}", privateKeyString);
            log.debug("load keyId:{}", keyId);
            RSAKey rsaKey = new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(keyId)
                    .build();

            var keyId2 = "nginx-creed-mall";
            keyPair = loadJksKeyPair(keyStore, "changeit", keyId2);
            publicKey = (RSAPublicKey) keyPair.getPublic();
            privateKey = (RSAPrivateKey) keyPair.getPrivate();
            log.debug("load keyId2:{}", keyId2);
            RSAKey rsaKey2 = new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(keyId2)
                    .build();
            JWKSet jwkSet = new JWKSet(Arrays.asList(rsaKey, rsaKey2));
            return new ImmutableJWKSet<>(jwkSet);
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }

    public KeyPair loadKeyPair(InputStream privateKeyIn, InputStream publicKeyIn, String algorithm) {
        try {
            return new KeyPair(loadPublicKey(publicKeyIn, algorithm), loadPrivateKey(privateKeyIn, algorithm));
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }

    public KeyStore loadKeyStore(InputStream in, String password) throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(in, password.toCharArray());
        return keyStore;
    }
    public KeyPair loadJksKeyPair(KeyStore keyStore, String password, String alias) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        RSAPrivateKey privateKey = (RSAPrivateKey) keyStore.getKey(alias, password.toCharArray());
        RSAPublicKey publicKey = (RSAPublicKey) keyStore.getCertificate(alias).getPublicKey();
        return new KeyPair(publicKey, privateKey);
    }
    public PrivateKey loadPrivateKey(InputStream in, String algorithm) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        String pkcs8PemString = IOUtils.toString(in, StandardCharsets.UTF_8.displayName())
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte [] pkcs8EncodedBytes = Base64.getDecoder().decode(pkcs8PemString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePrivate(keySpec);
    }

    public PublicKey loadPublicKey(InputStream in, String algorithm) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String x509PemString = IOUtils.toString(in, StandardCharsets.UTF_8.displayName())
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte [] x509EncodedBytes = Base64.getDecoder().decode(x509PemString);
        X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(x509EncodedBytes);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(x509Spec);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(CreedOAuth2AuthorizationRepository creedOAuth2AuthorizationRepository, RegisteredClientRepository registeredClientRepository) {
        return new JpaOAuth2AuthorizationService(creedOAuth2AuthorizationRepository, registeredClientRepository);
    }
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(CreedOAuth2AuthorizationConsentRepository authorizationConsentRepository, RegisteredClientRepository registeredClientRepository) {
        return new JpaOAuth2AuthorizationConsentService(authorizationConsentRepository, registeredClientRepository);
    }
}
