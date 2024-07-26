package com.ethan.system.service.oauth2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientCreateReqVO;
import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientPageReqVO;
import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientUpdateReqVO;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2RegisteredClient;
import com.ethan.system.dal.repository.oauth2.CreedOAuth2RegisteredClientRepository;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.common.utils.collection.CollUtils.convertMap;
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

    /**
     * 定时执行 {@link #schedulePeriodicRefresh()} 的周期
     * 因为已经通过 Redis Pub/Sub 机制，所以频率不需要高
     */
    private static final long SCHEDULER_PERIOD = 5 * 60 * 1000L;

    /**
     * 客户端缓存
     * key：客户端编号 {@link OAuth2ClientDO#getClientId()} ()}
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @Getter // 解决单测
    @Setter // 解决单测
    private volatile Map<String, CreedOAuth2RegisteredClient> clientCache;
    /**
     * 缓存角色的最大更新时间，用于后续的增量轮询，判断是否有更新
     */
    @Getter
    private volatile ZonedDateTime maxUpdateTime;

    @Resource
    private CreedOAuth2RegisteredClientRepository oauth2ClientRepository;

    // @Resource
    // private OAuth2ClientProducer oauth2ClientProducer;

    /**
     * 初始化 {@link #clientCache} 缓存
     */
    @Override
    @PostConstruct
    public void initLocalCache() {
        // 获取客户端列表，如果有更新
        List<CreedOAuth2RegisteredClient> registeredClientList = loadOAuth2ClientIfUpdate(maxUpdateTime);
        if (CollUtil.isEmpty(registeredClientList)) {
            return;
        }

        // 写入缓存
        clientCache = convertMap(registeredClientList, CreedOAuth2RegisteredClient::getClientId);
        // maxUpdateTime = getMaxValue(registeredClientList, CreedOAuth2RegisteredClient::getUpdateTime); TODO
        log.info("[initLocalCache][初始化 OAuth2Client 数量为 {}]", registeredClientList.size());
    }

    @Scheduled(fixedDelay = SCHEDULER_PERIOD, initialDelay = SCHEDULER_PERIOD)
    public void schedulePeriodicRefresh() {
        initLocalCache();
    }

    /**
     * 如果客户端发生变化，从数据库中获取最新的全量客户端。
     * 如果未发生变化，则返回空
     *
     * @param maxUpdateTime 当前客户端的最大更新时间
     * @return 客户端列表
     */
    private List<CreedOAuth2RegisteredClient> loadOAuth2ClientIfUpdate(ZonedDateTime maxUpdateTime) {
        // 第一步，判断是否要更新。
        if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
            log.info("[loadOAuth2ClientIfUpdate][首次加载全量客户端]");
        } else { // 判断数据库中是否有更新的客户端
            if (oauth2ClientRepository.countByUpdateTimeGreaterThan(maxUpdateTime) == 0) {
                return null;
            }
            log.info("[loadOAuth2ClientIfUpdate][增量加载全量客户端]");
        }
        // 第二步，如果有更新，则从数据库加载所有客户端
        return oauth2ClientRepository.findAll();
    }

    @Override
    public String createOAuth2Client(OAuth2ClientCreateReqVO createReqVO) {
        validateClientIdExists(null, createReqVO.getClientId());
        // 插入 TODO
        CreedOAuth2RegisteredClient oauth2Client = null;//OAuth2ClientConvert.INSTANCE.convert(createReqVO);
        oauth2ClientRepository.save(oauth2Client);
        // 发送刷新消息
        // oauth2ClientProducer.sendOAuth2ClientRefreshMessage();
        return oauth2Client.getId();
    }

    @Override
    public void updateOAuth2Client(OAuth2ClientUpdateReqVO updateReqVO) {
        // 校验存在
        validateOAuth2ClientExists(updateReqVO.getId());
        // 校验 Client 未被占用
        validateClientIdExists(updateReqVO.getId(), updateReqVO.getClientId());

        // 更新
        CreedOAuth2RegisteredClient updateObj = null;//OAuth2ClientConvert.INSTANCE.convert(updateReqVO);
        oauth2ClientRepository.save(updateObj);
        // 发送刷新消息
        // oauth2ClientProducer.sendOAuth2ClientRefreshMessage();
    }

    @Override
    public void deleteOAuth2Client(String id) {
        // 校验存在
        validateOAuth2ClientExists(id);
        // 删除
        oauth2ClientRepository.deleteById(id);
        // 发送刷新消息
        // oauth2ClientProducer.sendOAuth2ClientRefreshMessage();
    }

    private void validateOAuth2ClientExists(String id) {
        if (oauth2ClientRepository.findById(id) == null) {
            throw exception(OAUTH2_CLIENT_NOT_EXISTS);
        }
    }

    @VisibleForTesting
    void validateClientIdExists(String id, String clientId) {
        Optional<CreedOAuth2RegisteredClient> client = oauth2ClientRepository.findByClientId(clientId);
        if (client.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的客户端
        if (id == null) {
            throw exception(OAUTH2_CLIENT_EXISTS);
        }
        if (!client.get().getId().equals(id)) {
            throw exception(OAUTH2_CLIENT_EXISTS);
        }
    }

    @Override
    public CreedOAuth2RegisteredClient getOAuth2Client(String id) {
        return oauth2ClientRepository.findById(id).orElse(null);
    }

    @Override
    public PageResult<CreedOAuth2RegisteredClient> getOAuth2ClientPage(OAuth2ClientPageReqVO pageReqVO) {
        Page<CreedOAuth2RegisteredClient> page = oauth2ClientRepository.findAll(PageRequest.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()));
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    @Override
    public CreedOAuth2RegisteredClient validOAuthClientFromCache(String clientId, String clientSecret,
                                                    String authorizedGrantType, Collection<String> scopes, String redirectUri) {
        // 校验客户端存在、且开启
        CreedOAuth2RegisteredClient client = clientCache.get(clientId);
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

}
