package com.ethan.std.provider;

import com.ethan.std.provisioning.OauthAccessToken;
import com.ethan.std.provisioning.OauthAccessTokenRepository;
import com.ethan.std.provisioning.OauthRefreshToken;
import com.ethan.std.provisioning.OauthRefreshTokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/4/2022 5:57 PM
 */
public class CustomizeTokenStore implements TokenStore {
    private static final Logger log = LoggerFactory.getLogger(CustomizeTokenStore.class);
    private OauthAccessTokenRepository tokenRepository;
    private OauthRefreshTokenRepository refreshTokenRepository;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        OAuth2Authentication authentication = null;

        try {
            authentication = tokenRepository.findAuthentication(extractTokenKey(token))
                    .map(OauthAccessToken::getAuthentication)
                    .map(this::deserializeAuthentication)
                    .orElse(null);
        } catch (EmptyResultDataAccessException e) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find access token for token (readAuthentication) " + token);
            }
        } catch (IllegalArgumentException e) {
            log.warn("Failed to deserialize access token for " + token, e);
            removeRefreshToken(token);
        }

        return authentication;
    }

    @Override
    @Transactional
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String refreshToken = null;
        if (token.getRefreshToken() != null) {
            refreshToken = token.getRefreshToken().getValue();
        }

        // if (readAccessToken(token.getValue())!=null) {
        //     removeAccessToken(token.getValue());
        // }
        OAuth2AccessToken oAuth2AccessToken = readAccessToken(token.getValue());
        OauthAccessToken oauthToken;
        if (oAuth2AccessToken == null) {
            oauthToken = new OauthAccessToken();
        } else {
            oauthToken = tokenRepository.findToken(extractTokenKey(token.getValue())).get();
        }

        // OauthAccessToken oauthToken = new OauthAccessToken();
        oauthToken.setTokenId(extractTokenKey(token.getValue()));
        oauthToken.setToken(serializeAccessToken(token));
        oauthToken.setAuthenticationId(authenticationKeyGenerator.extractKey(authentication));
        oauthToken.setUserName(authentication.isClientOnly() ? null : authentication.getName());
        oauthToken.setClientId(authentication.getOAuth2Request().getClientId());
        oauthToken.setAuthentication(serializeAuthentication(authentication));
        oauthToken.setRefreshToken(extractTokenKey(refreshToken));
        oauthToken.setCreateTime(LocalDateTime.now());
        tokenRepository.save(oauthToken);

    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken accessToken = null;

        try {
            accessToken = tokenRepository.findToken(extractTokenKey(tokenValue))
                    .map(OauthAccessToken::getToken)
                    .map(this::deserializeAccessToken)
                    .orElse(null);
        } catch (EmptyResultDataAccessException e) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find access token for token (readAccessToken) " + tokenValue);
            }
        } catch (IllegalArgumentException e) {
            log.warn("Failed to deserialize access token for " + tokenValue, e);
            removeAccessToken(tokenValue);
        }

        return accessToken;
    }

    public void removeAccessToken(String tokenValue) {
        tokenRepository.deleteByTokenId(extractTokenKey(tokenValue));
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        removeAccessToken(token.getValue());
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        OauthRefreshToken oauthRefreshToken = new OauthRefreshToken();
        oauthRefreshToken.setTokenId(extractTokenKey(refreshToken.getValue()));
        oauthRefreshToken.setToken(serializeRefreshToken(refreshToken));
        oauthRefreshToken.setAuthentication(serializeAuthentication(authentication));
        oauthRefreshToken.setCreateTime(LocalDateTime.now());

        refreshTokenRepository.save(oauthRefreshToken);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        OAuth2RefreshToken refreshToken = null;
        try {
            refreshToken = refreshTokenRepository.findToken(extractTokenKey(tokenValue))
                    .map(OauthRefreshToken::getToken)
                    .map(this::deserializeRefreshToken)
                    .orElse(null);
        } catch (EmptyResultDataAccessException e) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find refresh access token for token " + tokenValue);
            }
        }
        catch (IllegalArgumentException e) {
            log.warn("Failed to deserialize refresh token for token" + tokenValue, e);
            removeRefreshToken(tokenValue);
        }

        return refreshToken;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(String tokenValue) {
        OAuth2Authentication authentication = null;
        try {
            authentication = refreshTokenRepository.findToken(extractTokenKey(tokenValue))
                    .map(OauthRefreshToken::getAuthentication)
                    .map(this::deserializeAuthentication)
                    .orElse(null);
        } catch (EmptyResultDataAccessException e) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find access token for token " + tokenValue);
            }
        } catch (IllegalArgumentException e) {
            log.warn("Failed to deserialize access token for " + tokenValue, e);
            removeRefreshToken(tokenValue);
        }

        return authentication;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        removeRefreshToken(token.getValue());
    }

    public void removeRefreshToken(String token) {
        refreshTokenRepository.deleteById(extractTokenKey(token));
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }
    public void removeAccessTokenUsingRefreshToken(String refreshToken) {
        tokenRepository.deleteByRefreshToken(refreshToken);
    }


    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = null;

        String key = authenticationKeyGenerator.extractKey(authentication);
        try {
            accessToken = tokenRepository.findFirstByAuthenticationIdOrderByCreateTimeDesc(key)
                    .map(OauthAccessToken::getToken)
                    .map(this::deserializeAccessToken)
                    .orElse(null);
        } catch (EmptyResultDataAccessException e) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find access token for authentication " + authentication);
            }
        } catch (IllegalArgumentException e) {
            log.warn("Could not extract access token for authentication " + authentication, e);
        }

        if (accessToken != null
                && !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
            removeAccessToken(accessToken.getValue());
            // Keep the store consistent (maybe the same user is represented by this authentication but the details have
            // changed)
            storeAccessToken(accessToken, authentication);
        }
        return accessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<>();

        try {
            accessTokens = tokenRepository.findTokenByClientIdAndUserName(userName, clientId)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(OauthAccessToken::getToken)
                    .map(this::deserializeAccessToken)
                    .collect(Collectors.toList());
        }
        catch (IllegalArgumentException e) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find access token for clientId " + clientId + " and userName " + userName);
            }
        }
        accessTokens = removeNulls(accessTokens);

        return accessTokens;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<>();

        try {
            accessTokens = tokenRepository.findTokenByClientId(clientId)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(OauthAccessToken::getToken)
                    .map(this::deserializeAccessToken)
                    .collect(Collectors.toList());
        }
        catch (IllegalArgumentException e) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find access token for clientId " + clientId);
            }
        }
        accessTokens = removeNulls(accessTokens);

        return accessTokens;
    }

    @Autowired
    public void setTokenRepository(OauthAccessTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Autowired
    public void setRefreshTokenRepository(OauthRefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // @Autowired
    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    private List<OAuth2AccessToken> removeNulls(List<OAuth2AccessToken> accessTokens) {
        List<OAuth2AccessToken> tokens = new ArrayList<>();
        for (OAuth2AccessToken token : accessTokens) {
            if (token != null) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    protected String extractTokenKey(String value) {
        if (value == null) {
            return null;
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }

        byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        return String.format("%032x", new BigInteger(1, bytes));
    }


    protected byte[] serializeAccessToken(OAuth2AccessToken token) {
        return serializationStrategy.serialize(token);
    }
    protected OAuth2AccessToken deserializeAccessToken(byte[] token) {
        return serializationStrategy.deserialize(token, OAuth2AccessToken.class);
    }
    protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
        return serializationStrategy.serialize(authentication);
    }
    protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
        return serializationStrategy.deserialize(authentication, OAuth2Authentication.class);
    }
    protected byte[] serializeRefreshToken(OAuth2RefreshToken refreshToken) {
        return serializationStrategy.serialize(refreshToken);
    }
    protected OAuth2RefreshToken deserializeRefreshToken(byte[] refreshToken) {
        return serializationStrategy.deserialize(refreshToken, OAuth2RefreshToken.class);
    }

    public void setSerializationStrategy(RedisTokenStoreSerializationStrategy serializationStrategy) {
        this.serializationStrategy = serializationStrategy;
    }
}
