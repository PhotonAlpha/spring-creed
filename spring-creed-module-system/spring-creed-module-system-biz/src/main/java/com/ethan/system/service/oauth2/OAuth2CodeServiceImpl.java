package com.ethan.system.service.oauth2;

import cn.hutool.core.util.IdUtil;
import com.ethan.common.utils.date.DateUtils;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2Authorization;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Calendar;
import java.util.List;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.constant.ErrorCodeConstants.OAUTH2_CODE_EXPIRE;
import static com.ethan.system.constant.ErrorCodeConstants.OAUTH2_CODE_NOT_EXISTS;

/**
 * OAuth2.0 授权码 Service 实现类
 * TODO
 * 
 */
@Service
@Validated
public class OAuth2CodeServiceImpl implements OAuth2CodeService {

    /**
     * 授权码的过期时间，默认 5 分钟
     */
    private static final Integer TIMEOUT = 5 * 60;

    // @Resource
    // private OAuth2CodeRepository oauth2CodeRepository;

    @Override
    public CreedOAuth2Authorization createAuthorizationCode(Long userId, Integer userType, String clientId,
                                                List<String> scopes, String redirectUri, String state) {
        // CreedOAuth2Authorization codeDO = new CreedOAuth2Authorization().setCode(generateCode())
        //         .setUserId(userId).setUserType(userType)
        //         .setClientId(clientId).setScopes(scopes)
        //         .setExpiresTime(DateUtils.addDate(Calendar.SECOND, TIMEOUT))
        //         .setRedirectUri(redirectUri).setState(state);
        // oauth2CodeRepository.save(codeDO);
        return null;
    }
    @Override
    public CreedOAuth2Authorization consumeAuthorizationCode(String code) {
        // CreedOAuth2Authorization codeDO = oauth2CodeRepository.findByCode(code);
        // if (codeDO == null) {
        //     throw exception(OAUTH2_CODE_NOT_EXISTS);
        // }
        // if (DateUtils.isExpired(codeDO.getExpiresTime())) {
        //     throw exception(OAUTH2_CODE_EXPIRE);
        // }
        // oauth2CodeRepository.deleteById(codeDO.getId());
        return null;
    }

    private static String generateCode() {
        return IdUtil.fastSimpleUUID();
    }

}
