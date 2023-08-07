package com.ethan.system.service.oauth2;

import cn.hutool.core.util.ObjectUtil;
import com.ethan.common.constant.UserTypeEnum;
import com.ethan.common.exception.enums.ResponseCodeEnum;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.date.DateUtils;
import com.ethan.security.oauth2.entity.CreedOAuth2RegisteredClient;
import com.ethan.security.oauth2.entity.client.CreedOAuth2AuthorizedClient;
import com.ethan.security.oauth2.repository.client.CreedOAuth2AuthorizedClientRepository;
import com.ethan.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenPageReqVO;
import com.ethan.system.dal.redis.oauth2.OAuth2AccessTokenRedisDAO;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception0;

/**
 * OAuth2.0 Token Service 实现类
 *
 * 
 */
@Service
public class OAuth2TokenServiceImpl implements OAuth2TokenService {

    // @Resource
    // private OAuth2AccessTokenRepository oauth2AccessTokenRepository;
    // @Resource
    // private OAuth2RefreshTokenRepository oauth2RefreshTokenRepository;
    @Resource
    private CreedOAuth2AuthorizedClientRepository authorizedClientRepository;

    @Resource
    private OAuth2AccessTokenRedisDAO oauth2AccessTokenRedisDAO;

    @Resource
    private OAuth2ClientService oauth2ClientService;


    @Resource
    private OAuth2AuthorizedClientManager authorizedClientManager;


    @Override
    @Transactional
    public CreedOAuth2AuthorizedClient createAccessToken(String userId, Integer userType, String clientId, List<String> scopes) {
        CreedOAuth2RegisteredClient registeredClient = oauth2ClientService.validOAuthClientFromCache(clientId);
        // 创建刷新令牌
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("okta")
                .principal(userId)
                .build();
        OAuth2AuthorizedClient authorize = authorizedClientManager.authorize(authorizeRequest);
        if (Objects.isNull(authorize)) {
            throw exception0(ResponseCodeEnum.UNAUTHORIZED.getCode(), "访问此资源需要完全的身份验证");
        }
        // OAuth2RefreshTokenDO refreshTokenDO = createOAuth2RefreshToken(userId, userType, clientDO, scopes);
        // 创建访问令牌
        return createOAuth2AccessToken(authorize, registeredClient);
    }

    private CreedOAuth2AuthorizedClient createOAuth2AccessToken(OAuth2AuthorizedClient authorize, CreedOAuth2RegisteredClient registeredClient) {
        OAuth2AccessToken accessToken = authorize.getAccessToken();
        OAuth2RefreshToken refreshToken = authorize.getRefreshToken();
        String refreshTokenVal = Optional.ofNullable(refreshToken).map(AbstractOAuth2Token::getTokenValue).orElse("");
        Instant issueTimeVal = Optional.ofNullable(refreshToken).map(AbstractOAuth2Token::getIssuedAt).orElse(null);
        Instant expiresTimeVal = Optional.ofNullable(refreshToken).map(AbstractOAuth2Token::getExpiresAt).orElse(null);


/*         Duration timeToLive = TokenSettings.withSettings(registeredClient.getClientSettings()).build()
                .getAccessTokenTimeToLive(); */
        Optional<CreedOAuth2AuthorizedClient> authorizedClientOptional = authorizedClientRepository.findByAccessTokenValue(accessToken.getTokenValue());
        CreedOAuth2AuthorizedClient accessTokenDO;
        if (authorizedClientOptional.isEmpty()) {
            accessTokenDO = new CreedOAuth2AuthorizedClient()
                    .setUserId(authorize.getPrincipalName())
                    .setUserType(UserTypeEnum.ADMIN.getValue())
                    .setClientRegistrationId(registeredClient.getClientId())
                    .setPrincipalName(authorize.getPrincipalName())
                    .setAccessTokenType(accessToken.getTokenType().getValue())
                    .setAccessTokenValue(accessToken.getTokenValue())
                    .setAccessTokenScopes(registeredClient.getScopes())
                    .setAccessTokenIssuedAt(accessToken.getIssuedAt())
                    .setAccessTokenExpiresAt(accessToken.getExpiresAt())
                    .setRefreshTokenValue(refreshTokenVal)
                    .setRefreshTokenIssuedAt(issueTimeVal)
                    .setRefreshTokenExpiresAt(expiresTimeVal)
                    .setCreatedAt(Instant.now());
        } else {
            CreedOAuth2AuthorizedClient creedOAuth2AuthorizedClient = authorizedClientOptional.get();
            creedOAuth2AuthorizedClient
                    .setUserId(authorize.getPrincipalName())
                    .setUserType(UserTypeEnum.ADMIN.getValue())
                    .setClientRegistrationId(registeredClient.getClientId())
                    .setPrincipalName(authorize.getPrincipalName())
                    .setAccessTokenType(accessToken.getTokenType().getValue())
                    .setAccessTokenValue(accessToken.getTokenValue())
                    .setAccessTokenScopes(registeredClient.getScopes())
                    .setAccessTokenIssuedAt(accessToken.getIssuedAt())
                    .setAccessTokenExpiresAt(accessToken.getExpiresAt())
                    .setRefreshTokenValue(refreshTokenVal)
                    .setRefreshTokenIssuedAt(issueTimeVal)
                    .setRefreshTokenExpiresAt(expiresTimeVal);
            accessTokenDO = creedOAuth2AuthorizedClient;
        }

        authorizedClientRepository.save(accessTokenDO);

        // OAuth2AccessTokenDO accessTokenDO = new OAuth2AccessTokenDO().setAccessToken(accessToken.getTokenValue())
        //         .setUserId(authorize.getPrincipalName())
        //         .setUserType(UserTypeEnum.ADMIN.getValue())
        //         // .setUserType(refreshTokenDO.getUserType())
        //         .setClientId(registeredClient.getClientId()).setScopes(registeredClient.getScopes())
        //         .setRefreshToken(Optional.ofNullable(refreshToken).map(AbstractOAuth2Token::getTokenValue).orElse(""))
        //         .setExpiresTime(Date.from(accessToken.getExpiresAt()));
        // accessTokenDO.setTenantId(TenantContextHolder.getTenantId()); // 手动设置租户编号，避免缓存到 Redis 的时候，无对应的租户编号
        // oauth2AccessTokenRepository.save(accessTokenDO);
        // 记录到 Redis 中
        oauth2AccessTokenRedisDAO.set(accessTokenDO);
        return accessTokenDO;
    }

    @Override
    public CreedOAuth2AuthorizedClient refreshAccessToken(String refreshToken, String clientId) {
        // 查询访问令牌
        Optional<CreedOAuth2AuthorizedClient> refreshTokenOptional = authorizedClientRepository.findByRefreshTokenValue(refreshToken);
        if (refreshTokenOptional.isEmpty()) {
            throw exception0(ResponseCodeEnum.BAD_REQUEST.getCode(), "无效的刷新令牌");
        }
        String clientRegistrationId = refreshTokenOptional.map(CreedOAuth2AuthorizedClient::getClientRegistrationId).orElse(StringUtils.EMPTY);

        // 校验 Client 匹配
        CreedOAuth2RegisteredClient clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        if (ObjectUtil.notEqual(clientId, clientRegistrationId)) {
            throw exception0(ResponseCodeEnum.BAD_REQUEST.getCode(), "刷新令牌的客户端编号不正确");
        }

        // 移除相关的访问令牌
        // List<OAuth2AccessTokenDO> accessTokenDOs = oauth2AccessTokenRepository.findByRefreshToken(refreshToken);
        // if (CollUtil.isNotEmpty(accessTokenDOs)) {
            // oauth2AccessTokenRepository.deleteAllById(convertSet(accessTokenDOs, OAuth2AccessTokenDO::getId));
            oauth2AccessTokenRedisDAO.delete(refreshToken);
        // }

        // 已过期的情况下，删除刷新令牌 TODO 需要确认
        // if (DateUtils.isExpired(refreshTokenDO.getExpiresTime())) {
        //     oauth2RefreshTokenRepository.deleteById(refreshTokenDO.getId());
        //     throw exception0(ResponseCodeEnum.UNAUTHORIZED.getCode(), "刷新令牌已过期");
        // }
        // 创建刷新令牌
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("okta")
                .principal(clientRegistrationId)
                .build();
        OAuth2AuthorizedClient authorize = authorizedClientManager.authorize(authorizeRequest);

        // 创建访问令牌
        return createOAuth2AccessToken(authorize, clientDO);
    }

    @Override
    public CreedOAuth2AuthorizedClient getAccessToken(String accessToken) {
        // 优先从 Redis 中获取
        CreedOAuth2AuthorizedClient accessTokenDO = oauth2AccessTokenRedisDAO.get(accessToken);
        if (accessTokenDO != null) {
            return accessTokenDO;
        }

        // 获取不到，从 MySQL 中获取
        Optional<CreedOAuth2AuthorizedClient> accessTokenOptional = authorizedClientRepository.findByAccessTokenValue(accessToken);
        // 如果在 MySQL 存在，则往 Redis 中写入
        if (accessTokenOptional.isPresent() && !DateUtils.isExpired(accessTokenOptional.get().getAccessTokenExpiresAt())) {
            oauth2AccessTokenRedisDAO.set(accessTokenDO);
        }
        return accessTokenDO;
    }

    @Override
    public CreedOAuth2AuthorizedClient checkAccessToken(String accessToken) {
        CreedOAuth2AuthorizedClient accessTokenDO = getAccessToken(accessToken);
        if (accessTokenDO == null) {
            throw exception0(ResponseCodeEnum.UNAUTHORIZED.getCode(), "访问令牌不存在");
        }
        if (DateUtils.isExpired(accessTokenDO.getAccessTokenExpiresAt())) {
            throw exception0(ResponseCodeEnum.UNAUTHORIZED.getCode(), "访问令牌已过期");
        }
        return accessTokenDO;
    }

    @Override
    public CreedOAuth2AuthorizedClient removeAccessToken(String accessToken) {
        // 删除访问令牌
        Optional<CreedOAuth2AuthorizedClient> accessTokenOptional = authorizedClientRepository.findByAccessTokenValue(accessToken);
        if (accessTokenOptional.isEmpty()) {
            return null;
        }
        authorizedClientRepository.deleteById(accessTokenOptional.get().getId());
        oauth2AccessTokenRedisDAO.delete(accessToken);
        // 删除刷新令牌
        // oauth2RefreshTokenRepository.deleteByRefreshToken(accessTokenDO.getRefreshToken());
        return accessTokenOptional.get();
    }

    @Override
    public PageResult<CreedOAuth2AuthorizedClient> getAccessTokenPage(OAuth2AccessTokenPageReqVO reqVO) {
        Page<CreedOAuth2AuthorizedClient> page = authorizedClientRepository.findAll(getAccessTokenPageSpecification(reqVO), PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize()));
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    private static Specification<CreedOAuth2AuthorizedClient> getAccessTokenPageSpecification(OAuth2AccessTokenPageReqVO reqVO) {
        return (Specification<CreedOAuth2AuthorizedClient>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (Objects.nonNull(reqVO.getUserId())) {
                predicateList.add(cb.equal(root.get("user_id"), reqVO.getUserId()));
            }
            if (Objects.nonNull(reqVO.getUserType())) {
                predicateList.add(cb.equal(root.get("user_type"), reqVO.getUserType()));
            }
            if (StringUtils.isNotBlank(reqVO.getClientId())) {
                predicateList.add(cb.like(root.get("client_id"),
                        "%" + reqVO.getClientId() + "%"));
            }
            if (StringUtils.isNotBlank(reqVO.getClientId())) {
                predicateList.add(cb.greaterThan(root.get("expires_time"), new Date()));
            }
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
