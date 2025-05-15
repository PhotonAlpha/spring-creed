package com.ethan.system.service.oauth2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientPageReqVO;
import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientSaveReqVO;
import com.ethan.system.convert.auth.OAuth2ClientConvert;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2RegisteredClient;
import com.ethan.system.dal.redis.RedisKeyConstants;
import com.ethan.system.dal.repository.oauth2.CreedOAuth2RegisteredClientRepository;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.config.OAuth2CacheManagerConfiguration.OAUTH2_CACHE_MANAGER;
import static com.ethan.system.constant.ErrorCodeConstants.OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS;
import static com.ethan.system.constant.ErrorCodeConstants.OAUTH2_CLIENT_CLIENT_SECRET_ERROR;
import static com.ethan.system.constant.ErrorCodeConstants.OAUTH2_CLIENT_DISABLE;
import static com.ethan.system.constant.ErrorCodeConstants.OAUTH2_CLIENT_EXISTS;
import static com.ethan.system.constant.ErrorCodeConstants.OAUTH2_CLIENT_NOT_EXISTS;
import static com.ethan.system.constant.ErrorCodeConstants.OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH;
import static com.ethan.system.constant.ErrorCodeConstants.OAUTH2_CLIENT_SCOPE_OVER;


/**
 * OAuth2.0 Client Service 实现类
 *
 * 
 */
@Service
@Validated
@Slf4j
public class OAuth2ClientServiceImpl implements OAuth2ClientService {
    @Resource
    private CreedOAuth2RegisteredClientRepository oauth2ClientRepository;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    @Lazy
    private OAuth2ClientService self;

    @Override
    public String createOAuth2Client(OAuth2ClientSaveReqVO createReqVO) {
        validateClientIdExists(null, createReqVO.getClientId());
        // 插入
        CreedOAuth2RegisteredClient client = OAuth2ClientConvert.INSTANCE.convert(createReqVO);
        client.setId(UUID.randomUUID().toString());
        client.setClientSecret(passwordEncoder.encode(createReqVO.getSecret()));

        oauth2ClientRepository.save(client);
        return client.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.OAUTH_CLIENT,
            allEntries = true) // allEntries 清空所有缓存，因为可能修改到 clientId 字段，不好清理
    public void updateOAuth2Client(OAuth2ClientSaveReqVO updateReqVO) {
        // 校验存在
        validateOAuth2ClientExists(updateReqVO.getId());
        // 校验 Client 未被占用
        var registeredClient = validateClientIdExists(updateReqVO.getId(), updateReqVO.getClientId());

        // 更新
        OAuth2ClientConvert.INSTANCE.update(updateReqVO, registeredClient);
        oauth2ClientRepository.save(registeredClient);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.OAUTH_CLIENT,
            allEntries = true) // allEntries 清空所有缓存，因为 id 不是直接的缓存 key，不好清理
    public void deleteOAuth2Client(String id) {
        // 校验存在
        validateOAuth2ClientExists(id);
        // 删除
        oauth2ClientRepository.deleteById(id);
    }

    private void validateOAuth2ClientExists(String id) {
        if (!oauth2ClientRepository.existsById(id)) {
            throw exception(OAUTH2_CLIENT_NOT_EXISTS);
        }
    }

    @VisibleForTesting
    CreedOAuth2RegisteredClient validateClientIdExists(String id, String clientId) {
        Optional<CreedOAuth2RegisteredClient> client = oauth2ClientRepository.findByClientId(clientId);
        //如果是新增，直接返回
        if (client.isEmpty()) {
            return null;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的客户端
        if (id == null) {
            throw exception(OAUTH2_CLIENT_EXISTS);
        }
        // 校验 Client 未被占用
        Predicate<CreedOAuth2RegisteredClient> clientPredicate = cli -> StringUtils.equals(cli.getId(), id);
        return client.filter(clientPredicate).orElseThrow(() -> exception(OAUTH2_CLIENT_EXISTS));
    }

    @Override
    public CreedOAuth2RegisteredClient getOAuth2Client(String id) {
        return oauth2ClientRepository.findById(id).orElse(null);
    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.OAUTH_CLIENT, key = "#id",
            unless = "#result == null", cacheManager = OAUTH2_CACHE_MANAGER)
    public CreedOAuth2RegisteredClient getOAuth2ClientFromCacheById(String id) {
        log.info("hit getOAuth2ClientFromCacheById:{}", id);
        return oauth2ClientRepository.findById(id).orElse(null);
    }

    @Override
    public PageResult<CreedOAuth2RegisteredClient> getOAuth2ClientPage(OAuth2ClientPageReqVO pageReqVO) {
        Page<CreedOAuth2RegisteredClient> page = oauth2ClientRepository.findByCondition(pageReqVO, PageRequest.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()));
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    @Override
    public List<CreedOAuth2RegisteredClient> findAllOAuth2Client(OAuth2ClientPageReqVO pageReqVO) {
        return oauth2ClientRepository.findByAllCondition(pageReqVO);
    }

    @Override
    public CreedOAuth2RegisteredClient validOAuthClientFromCache(String clientId, String clientSecret,
                                                    String authorizedGrantType, Collection<String> scopes, String redirectUri) {
        // 校验客户端存在、且开启
        // 校验客户端存在、且开启
        var client = self.getOAuth2ClientFromCache(clientId);

        if (client == null) {
            throw exception(OAUTH2_CLIENT_NOT_EXISTS);
        }
        if (ObjectUtil.notEqual(client.getEnabled(), CommonStatusEnum.ENABLE)) {
            throw exception(OAUTH2_CLIENT_DISABLE);
        }

        // 校验客户端密钥
        if (StrUtil.isNotEmpty(clientSecret) && ObjectUtil.notEqual(client.getClientSecret(), clientSecret)) {
            throw exception(OAUTH2_CLIENT_CLIENT_SECRET_ERROR);
        }
        // 校验授权方式
        if (StrUtil.isNotEmpty(authorizedGrantType) && !CollUtil.contains(client.getAuthorizationGrantTypes(), authorizedGrantType)) {
            throw exception(OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS);
        }
        // 校验授权范围
        if (CollUtil.isNotEmpty(scopes) && !CollUtil.containsAll(client.getScopes(), scopes)) {
            throw exception(OAUTH2_CLIENT_SCOPE_OVER);
        }
        // 校验回调地址
        if (StrUtil.isNotEmpty(redirectUri) && !StringUtils.startsWithAny(redirectUri, client.getRedirectUris().toArray(String[]::new))) {
            throw exception(OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH, redirectUri);
        }
        return client;
    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.OAUTH_CLIENT, key = "#clientId",
            unless = "#result == null", cacheManager = OAUTH2_CACHE_MANAGER)
    public CreedOAuth2RegisteredClient getOAuth2ClientFromCache(String clientId) {
        return oauth2ClientRepository.findByClientId(clientId).orElse(null);
    }
}
