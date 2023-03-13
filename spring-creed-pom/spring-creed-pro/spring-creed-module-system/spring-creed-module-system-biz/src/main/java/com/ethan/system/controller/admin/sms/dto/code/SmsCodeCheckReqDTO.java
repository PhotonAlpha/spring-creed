package com.ethan.system.controller.admin.sms.dto.code;

import com.ethan.common.validation.InEnum;
import com.ethan.common.validation.Mobile;
import com.ethan.system.api.constant.sms.SmsSceneEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 短信验证码的校验 Request DTO
 *
 * 
 */
@Data
public class SmsCodeCheckReqDTO {

    /**
     * 手机号
     */
    @Mobile
    @NotEmpty(message = "手机号不能为空")
    private String mobile;
    /**
     * 发送场景
     */
    @NotNull(message = "发送场景不能为空")
    @InEnum(SmsSceneEnum.class)
    private Integer scene;
    /**
     * 验证码
     */
    @NotEmpty(message = "验证码")
    private String code;

}
