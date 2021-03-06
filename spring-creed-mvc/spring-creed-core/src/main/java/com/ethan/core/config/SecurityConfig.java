package com.ethan.core.config;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEncryptableProperties
public class SecurityConfig {
  @Bean("jasyptStringEncryptor")
  public StringEncryptor stringEncryptor() {
    String password = "loooooooooogpassword";
    PooledPBEStringEncryptor encrytor = new PooledPBEStringEncryptor();
    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
    config.setPassword(password);
    config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
    config.setKeyObtentionIterations("1000");
    config.setPoolSize(1);
    config.setProviderName("SunJCE");
    config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
    config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
    config.setStringOutputType("base64");
    encrytor.setConfig(config);
    /**
     * customer encryption algorithm class
     *
     * StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
     * encryptor.setProvider(new BouncyCastleProvider());
     * encryptor.setAlgorithm("PBEWITHSHA256AND256BITAES-CBC-BC");
     * encryptor.setPassword(password);
     * encryptor.setStringOutputType("base64");
     * */
    return encrytor;
  }
  @Bean(name = "encryptablePropertyDetector")
  public EncryptablePropertyDetector encryptablePropertyDetector() {
    return new CustomerEncryptPropertyDetector();
  }
}
