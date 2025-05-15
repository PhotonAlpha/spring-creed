package com.ethan.example.config;

import org.springframework.cloud.config.server.encryption.KeyStoreTextEncryptorLocator;
import org.springframework.cloud.config.server.encryption.TextEncryptorLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.encrypt.KeyStoreKeyFactory;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 7/5/25
 */
@Configuration
public class SecurityConfig {
    @Bean
    public TextEncryptorLocator textEncryptorLocator() {
        var keyId = "server-creed-mall";
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("ssl/creed-mall-server.jks"), "changeit".toCharArray(), keyId);
        return new KeyStoreTextEncryptorLocator(factory, "changeit", keyId);
    }
}
