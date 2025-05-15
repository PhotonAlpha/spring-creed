package com.ethan.infra.api.config;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 参数配置 API 实现类
 *
 */
@Service
@Validated
public class ConfigApiImpl implements ConfigApi {

    // @Resource
    // private ConfigService configService;

    @Override
    public String getConfigValueByKey(String key) {
        return "password";
        // ConfigDO config = configService.getConfigByKey(key);
        // return config != null ? config.getValue() : null;
    }

}
