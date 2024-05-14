package com.ethan.utils;

import com.nimbusds.jose.jwk.RSAKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class JwtTokenUtil {

    /**
     * 寻找证书文件
     */
    private static PrivateKey privateKey = null;
    private static PublicKey publicKey = null;

    static { // 将证书文件里边的私钥公钥拿出来
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();

            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
            RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();

        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }


    /**
     * @author Administrator
     * @date 2021/3/9 17:36
     * @description 生成jwt token
     */
    public static String generateToken(Map<String, Object> claims, String subject, int expiration) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
        // 生成签名密钥
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate, expiration);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(signatureAlgorithm, privateKey)
                .compact();
    }

    private static Date calculateExpirationDate(Date createdDate, int expiration) {
        return new Date(createdDate.getTime() + expiration);
    }

    /**
     * 解密Jwt内容
     *
     * @param jwt jwt
     * @return Claims
     */
    public static Claims parseJwtRsa256(String jwt) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

}
