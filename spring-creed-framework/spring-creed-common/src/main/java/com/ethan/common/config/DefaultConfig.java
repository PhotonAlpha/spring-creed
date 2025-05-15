package com.ethan.common.config;

import com.ethan.common.common.SnowflakeIdWorker;
import com.ethan.common.common.SnowflakeProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultConfig {
    @Bean
    @ConditionalOnMissingBean(value = SnowflakeIdWorker.class)
    public SnowflakeIdWorker snowflakeIdWorker(SnowflakeProperties properties) {
        return new SnowflakeIdWorker(properties.getWorkerId(), properties.getDatacenterId());
    }
}
