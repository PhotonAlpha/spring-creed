package com.ethan.framework.web.config;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * Xss 配置属性
 *
 * 
 */
// @ConfigurationProperties(prefix = "yudao.xss")
// @Validated
@Data
public class XssProperties {

    /**
     * 是否开启，默认为 true
     */
    private boolean enable = true;
    /**
     * 需要排除的 URL，默认为空
     */
    private List<String> excludeUrls = Collections.emptyList();

}
