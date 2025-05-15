package com.ethan.common.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "snowflake")
public class SnowflakeProperties {
    /**
     * 工作节点ID
     */
    private Long workerId = 0L;
    /**
     * 数据中心ID
     */
    private Long datacenterId = 0L;
}
