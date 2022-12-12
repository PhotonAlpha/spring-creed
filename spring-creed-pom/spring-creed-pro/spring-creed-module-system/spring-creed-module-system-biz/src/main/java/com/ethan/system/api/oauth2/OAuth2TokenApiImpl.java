package com.ethan.system.api.oauth2;

import com.ethan.security.oauth2.OAuth2TokenApi;
import com.ethan.security.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import com.ethan.security.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import com.ethan.security.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuth2.0 Token API 实现类
 *
 * @author 芋道源码
 */
@Service
@Deprecated
public class OAuth2TokenApiImpl implements OAuth2TokenApi {
    public static final Map<String, OAuth2AccessTokenRespDTO> map = new ConcurrentHashMap();
    // @Resource
    // private OAuth2TokenService oauth2TokenService;

    @Override
    public OAuth2AccessTokenRespDTO createAccessToken(OAuth2AccessTokenCreateReqDTO reqDTO) {
        // OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(
        //         reqDTO.getUserId(), reqDTO.getUserType(), reqDTO.getClientId(), reqDTO.getScopes());
        // return OAuth2TokenConvert.INSTANCE.convert2(accessTokenDO);
        String tokenStr = UUID.randomUUID().toString();
        OAuth2AccessTokenRespDTO token = new OAuth2AccessTokenRespDTO();
        token.setAccessToken(tokenStr);
        token.setRefreshToken(tokenStr);


        Date expiredTime = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        token.setExpiresTime(expiredTime);
        token.setUserId(reqDTO.getUserId());
        token.setUserType(reqDTO.getUserType());

        map.put(tokenStr, token);
        return token;
    }

    @Override
    public OAuth2AccessTokenCheckRespDTO checkAccessToken(String accessToken) {
        // return OAuth2TokenConvert.INSTANCE.convert(oauth2TokenService.checkAccessToken(accessToken));
        OAuth2AccessTokenCheckRespDTO respDTO = new OAuth2AccessTokenCheckRespDTO();
        OAuth2AccessTokenRespDTO token = map.get(accessToken);
        // respDTO.setScopes(token.);
        respDTO.setUserType(token.getUserType());
        respDTO.setUserId(token.getUserId());
        return respDTO;
    }

    @Override
    public OAuth2AccessTokenRespDTO removeAccessToken(String accessToken) {
        // OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.removeAccessToken(accessToken);
        // return OAuth2TokenConvert.INSTANCE.convert2(accessTokenDO);
        return map.remove(accessToken);
    }

    @Override
    public OAuth2AccessTokenRespDTO refreshAccessToken(String refreshToken, String clientId) {
        // OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.refreshAccessToken(refreshToken, clientId);
        // return OAuth2TokenConvert.INSTANCE.convert2(accessTokenDO);
        return map.get(refreshToken);
    }

}
