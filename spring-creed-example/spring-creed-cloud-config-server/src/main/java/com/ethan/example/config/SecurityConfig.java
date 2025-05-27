package com.ethan.example.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.server.encryption.KeyStoreTextEncryptorLocator;
import org.springframework.cloud.config.server.encryption.TextEncryptorLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.encrypt.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.text.ParseException;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 7/5/25
 */
@Configuration
@Slf4j
public class SecurityConfig {
    // @Value("${encrypt.secret:mySecretKey123456}")
    // private String sec;
    @Value("${encrypt.vault:AQBYhCGnz4TOImHL1KaFuzNCW+QXGK22V+VKGZmXGxikr6zNK3P09Ha/eMzzNQ60Z/4nePi54Yf19aqEn4dLJKtzd1aGBBdf726zBvpddfQmxDpNKiusB0WfPdNdg0B3D3K2iyIIFkc+ou2X9U4X7roo+3ZBbsKpVfIm00eQ+gDMllKBCgm04B/TF2IdBZj+Ylgn1PSZdD3s9V6+fpdHgzNbgEFpdH+9TLFPPyrH798ZhavIYYm6QSXIanf0kWIJVgGYw5IuQCaI5Xx6e9+YBIdxug8giIU8ZHgeAGpdSp0SG5YbLVQHILIjWg+FaO1rmOdoP/VkFptFtttxpnuiv7U6C2cS1gB+ho8Pvc++zKK5bdyxh69dOV48qTW3hjnxE+g=}")
    private String vault;
    @Value("${encrypt.defaultKeyId:nginx-creed-mall}")
    private String keyId;

    @Bean
    TextEncryptor textEncryptor() throws IOException, ParseException, JOSEException {
        // 创建 AES 密钥生成器
        /*
         KeyGenerator keyGen = KeyGenerator.getInstance("AES");
         keyGen.init(256); // 指定密钥长度（128/192/256）
         生成 SecretKey
         String secretKey = "mySecretKey123456"; // 16/24/32 字节
         String salt = KeyGenerators.string().generateKey(); // 生成随机盐
        */
        // return Encryptors.delux(sec, "f678603758eb9f6f");
        JWKSet jwkSet = JWKSet.parse(IOUtils.toString(new ClassPathResource("vault/jwks.json").getInputStream(), StandardCharsets.UTF_8));
        JWK jwk = jwkSet.getKeyByKeyId(keyId);
        RSAKey rsaKey = jwk.toRSAKey();
        return new RsaSecretEncryptor(new KeyPair(rsaKey.toPublicKey(), rsaKey.toPrivateKey()));
    }

    @Bean
    public TextEncryptorLocator textEncryptorLocator(TextEncryptor textEncryptor) {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("ssl/creed-mall-server.jks"), textEncryptor.decrypt(vault).toCharArray(), "jks");
        return new KeyStoreTextEncryptorLocator(factory, textEncryptor.decrypt(vault), keyId);
    }
}
