package com.ethan.identity.server.config;

import com.ethan.identity.server.filter.IdGenerateConsoleFilter;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 30/12/24
 */
@ConfigurationProperties(prefix = "leaf")
@Data
public class IdentityProperties {
    private SegmentProperties segment;
    private SnowflakeProperties snowflake;

    @Data
    public static class SegmentProperties {
        private Boolean enable = false;
    }
    @Data
    public static class SnowflakeProperties {
        private Boolean enable = false;
        private ZkProperties zk;
    }

    @Data
    public static class ZkProperties {
        private String address;
        private Integer port;
    }
}
