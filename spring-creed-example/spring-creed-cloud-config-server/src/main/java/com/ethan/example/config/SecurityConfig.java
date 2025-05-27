package com.ethan.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.server.encryption.KeyStoreTextEncryptorLocator;
import org.springframework.cloud.config.server.encryption.TextEncryptorLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.KeyStoreKeyFactory;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 7/5/25
 */
@Configuration
@Slf4j
public class SecurityConfig {
    @Value("${encrypt.secret:mySecretKey123456}")
    private String sec;
    @Value("${encrypt.vault:6608ca976d009fff5d6cb0ec6f83dcc06e3f88bfa6ae5cec603bbd79c916158569fb1e722d7bb1f1}")
    private String vault;
    @Value("${encrypt.defaultKeyId:server-creed-mall}")
    private String keyId;

    @Bean
    TextEncryptor textEncryptor() {
        // 创建 AES 密钥生成器
        /*
         KeyGenerator keyGen = KeyGenerator.getInstance("AES");
         keyGen.init(256); // 指定密钥长度（128/192/256）
         生成 SecretKey
         String secretKey = "mySecretKey123456"; // 16/24/32 字节
         String salt = KeyGenerators.string().generateKey(); // 生成随机盐
        */
        return Encryptors.delux(sec, "f678603758eb9f6f");
    }

    @Bean
    public TextEncryptorLocator textEncryptorLocator(TextEncryptor textEncryptor) {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("ssl/creed-mall-server.jks"), textEncryptor.decrypt(vault).toCharArray(), "jks");
        return new KeyStoreTextEncryptorLocator(factory, textEncryptor.decrypt(vault), keyId);
    }
}
