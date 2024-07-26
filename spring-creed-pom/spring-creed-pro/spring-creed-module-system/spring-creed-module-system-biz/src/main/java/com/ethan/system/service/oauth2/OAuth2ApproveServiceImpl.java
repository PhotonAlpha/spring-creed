package com.ethan.system.service.oauth2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.ethan.common.utils.date.DateUtils;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2RegisteredClient;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 批准 Service 实现类
 *
 * 
 */
@Service
@Validated
public class OAuth2ApproveServiceImpl implements OAuth2ApproveService {

    /**
     * 批准的过期时间，默认 30 天
     */
    private static final Integer TIMEOUT = 30 * 24 * 60 * 60; // 单位：秒

    @Resource
    private OAuth2ClientService oauth2ClientService;

    // @Resource
    // private OAuth2ApproveRepository oauth2ApproveRepository;

    @Override
    @Transactional
    public boolean checkForPreApproval(Long userId, Integer userType, String clientId, Collection<String> requestedScopes) {
        // 第一步，基于 Client 的自动授权计算，如果 scopes 都在自动授权中，则返回 true 通过
        CreedOAuth2RegisteredClient clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        Assert.notNull(clientDO, "客户端不能为空"); // 防御性编程
        // TODO getAutoApproveScopes
        if (CollUtil.containsAll(clientDO.getScopes(), requestedScopes)) {
            // gh-877 - if all scopes are auto approved, approvals still need to be added to the approval store.
            Date expireTime = DateUtils.addDate(Calendar.SECOND, TIMEOUT);
            for (String scope : requestedScopes) {
                saveApprove(userId, userType, clientId, scope, true, expireTime);
            }
            return true;
        }

        // 第二步，算上用户已经批准的授权。如果 scopes 都包含，则返回 true
        // List<OAuth2ApproveDO> approveDOs = getApproveList(userId, userType, clientId);
        // Set<String> scopes = convertSet(approveDOs, OAuth2ApproveDO::getScope,
        //         OAuth2ApproveDO::getApproved); // 只保留未过期的 + 同意的
        // return CollUtil.containsAll(scopes, requestedScopes);
        return true;
    }

    @Override
    @Transactional
    public boolean updateAfterApproval(Long userId, Integer userType, String clientId, Map<String, Boolean> requestedScopes) {
        // 如果 requestedScopes 为空，说明没有要求，则返回 true 通过
        if (CollUtil.isEmpty(requestedScopes)) {
            return true;
        }

        // 更新批准的信息
        boolean success = false; // 需要至少有一个同意
        Date expireTime = DateUtils.addDate(Calendar.SECOND, TIMEOUT);
        for (Map.Entry<String, Boolean> entry :requestedScopes.entrySet()) {
            if (entry.getValue()) {
                success = true;
            }
            saveApprove(userId, userType, clientId, entry.getKey(), entry.getValue(), expireTime);
        }
        return success;
    }

    @Override
    public List<CreedOAuth2RegisteredClient> getApproveList(Long userId, Integer userType, String clientId) {
        // List<OAuth2ApproveDO> approveDOs = oauth2ApproveRepository.findByUserIdAndUserTypeAndClientId(
        //         userId, userType, clientId);
        // approveDOs.removeIf(o -> DateUtils.isExpired(o.getExpiresTime()));
        // return approveDOs;
        return null;
    }

    @VisibleForTesting
    void saveApprove(Long userId, Integer userType, String clientId,
                     String scope, Boolean approved, Date expireTime) {
        // 先更新
        // OAuth2ApproveDO approveDO = new OAuth2ApproveDO().setUserId(userId).setUserType(userType)
        //         .setClientId(clientId).setScope(scope).setApproved(approved).setExpiresTime(expireTime);
        // // if (oauth2ApproveRepository.save(approveDO) == 1) {
        // //     return;
        // // }
        // // 失败，则说明不存在，进行更新
        // oauth2ApproveRepository.save(approveDO);
    }

}
