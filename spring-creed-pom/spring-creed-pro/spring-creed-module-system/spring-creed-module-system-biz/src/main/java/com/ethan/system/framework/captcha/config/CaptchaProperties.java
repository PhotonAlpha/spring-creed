package com.ethan.system.framework.captcha.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "creed.captcha")
@Validated
@Data
public class CaptchaProperties {

    private static final Boolean ENABLE_DEFAULT = true;

    /**
     * 是否开启
     * 注意，这里仅仅是后端 Server 是否校验，暂时不控制前端的逻辑
     */
    private Boolean enable = ENABLE_DEFAULT;
    /**
     * 验证码的过期时间
     */
    @NotNull(message = "验证码的过期时间不为空")
    private Duration timeout = Duration.ofMinutes(2);
    /**
     * 验证码的高度
     */
    @NotNull(message = "验证码的高度不能为空")
    private Integer height = 60;
    /**
     * 验证码的宽度
     */
    @NotNull(message = "验证码的宽度不能为空")
    private Integer width = 160;

}
