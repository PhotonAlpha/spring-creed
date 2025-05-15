package com.ethan.example;

import com.ethan.example.dto.MyAccountInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.config.server.encryption.KeyStoreTextEncryptorLocator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.encrypt.KeyStoreKeyFactory;
import org.springframework.security.crypto.encrypt.RsaAlgorithm;
import org.springframework.security.crypto.encrypt.RsaSecretEncryptor;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.ServerException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 7/5/25
 */
@Slf4j
class RsaSecretEncryptorTest {
    @Test
    void encrypt() {

    }

    @Test
    void decrypt() {
        var keyId = "server-creed-mall";
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("ssl/creed-mall-server.jks"), "changeit".toCharArray(), keyId);
        KeyStoreTextEncryptorLocator textEncryptorLocator = new KeyStoreTextEncryptorLocator(factory, "changeit", keyId);
        // textEncryptorLocator.locate(Map.of())
        // textEncryptorLocator.
    }

    @Test
    void loadKeyStore() throws UnrecoverableKeyException, CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
        try (InputStream in = new ClassPathResource("ssl/creed-mall-server.jks").getInputStream()) {
            KeyStore keyStore = loadKeyStore(in, "changeit");
            var keyId = "server-creed-mall";
            KeyPair keyPair = loadJksKeyPair(keyStore, "changeit", keyId);
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            // RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            // 创建加密器
            RsaSecretEncryptor publicEncryptor = new RsaSecretEncryptor(publicKey);
            String encryptedPassword = publicEncryptor.encrypt("root");
            // AQBT+MKqAVzcwRfqtlF7niw9GFyVk6uPYV5itSE/g3EWjh7QQDXB2bM3RHQ3/wAnIX5+sOc5nl8+mXgWoj1Nma49Bbxh2yu7uzN9JxfAdnlTvOvq5OqzJlOhG2dLV1J+fAVzqn3xriHMCPIrDT+LL2S8Gvui9SYGXQYDyAko1HFmbRat+OCAXs5obhuODGq5CeKZ4iQ4ZpDDk7nlyrD30jUDziX4bq1v/JAEVCPAMRfscOWiLW+vJ0KaP6HnLY6s4JlxCiANle+ZUnBYE05fJ4egkn1vQslaeZaJ1/OxDFi+40Rx/eTWK1OuP1lU8XP0jILAtAQxQwpW+2IgeHbfNgcdJHzKN5lhSglnUV+DEqML5nOvxatydBzvZzvAbGE+6LE=
            // AQDXKiZOgQEx28rqWIlAmqDhTXBNkL5JQpKw6ZIg5qm3ajE66A8vKOObikTBDClngy/bmqPgaXkbyHDoTv0caAD+ynwBVWNmopy6VNMcQm/8aBfqMAD4oir/+olIpPsJhT8xpW1NJGehVkyESKEQmJCEhWRmUvzEP7bVBSL/pKgPYNbvO88+/8sC+rCZNcZ3gcCjokkwOs7Lfl63dLDXwpZFjOZS8/IepBWvygESfnxXfXo3RYsviSn/AT7ztksDajnU6OrJwFTO07NV0OMPWIjPXdXqga8o1TG43+HQSPDpEjuzgVv9bNmi2+spvBxoPbwVprGoDPH4btnsQ2NwZ7ODHf0ukZ7cFV5Uo+BHra3dgtNYohf/zoSiAYvV1Zm3aZU=
            log.info("encryptedPassword:{}", encryptedPassword);
            RsaSecretEncryptor privateDencryptor = new RsaSecretEncryptor(keyPair);
            var decryptPwd = privateDencryptor.decrypt(encryptedPassword);
            log.info("decryptPwd:{}", decryptPwd);
        } catch (Exception e) {
            log.error("load RSA failure", e);
            throw e;
        }
    }

    public KeyPair loadJksKeyPair(KeyStore keyStore, String password, String alias) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        RSAPrivateKey privateKey = (RSAPrivateKey) keyStore.getKey(alias, password.toCharArray());
        RSAPublicKey publicKey = (RSAPublicKey) keyStore.getCertificate(alias).getPublicKey();
        return new KeyPair(publicKey, privateKey);
    }
    public KeyStore loadKeyStore(InputStream in, String password) throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(in, password.toCharArray());
        return keyStore;
    }

    @Test
    void name() {
        MyAccountInfoVO account1 = new MyAccountInfoVO("1", "code_2b1901454668", "name",
                "password_aa48e0a698b0",
                "email_3c4b23ad7a91",
                "sex_7c01821012a2",
                "phone_c4f395e54311");
        MyAccountInfoVO account2 = new MyAccountInfoVO("1", "code_2b1901454668", "name",
                "password_aa48e0a698b0",
                "email_3c4b23ad7a91",
                "sex_7c01821012a2",
                "phone_c4f395e54311");
        HashMap<Object, Object> map = new HashMap<>();
        map.put(Pair.of(account1, "b"), account1);
        // System.out.println(Pair.of(account1, "b") == Pair.of(account2, "b"));
        System.out.println(map.get(Pair.of(account2, "b")));

    }
}
