package com.ethan.system.controller.admin.sms.dto.code;

import com.ethan.common.validation.InEnum;
import com.ethan.common.validation.Mobile;
import com.ethan.system.api.constant.sms.SmsSceneEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 短信验证码的发送 Request DTO
 *
 * 
 */
@Data
@Accessors(chain = true)
public class SmsCodeSendReqDTO {

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
     * 发送 IP
     */
    @NotEmpty(message = "发送 IP 不能为空")
    private String createIp;

}
