package com.ethan.system.controller.admin.social.dto;

import com.ethan.common.constant.UserTypeEnum;
import com.ethan.common.validation.InEnum;
import com.ethan.system.api.constant.social.SocialTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 社交绑定 Request DTO，使用 code 授权码
 *
 * 
 */
@Data
public class SocialUserUnbindReqDTO {

    /**
     * 用户编号
     */
    @NotNull(message = "用户编号不能为空")
    private Long userId;
    /**
     * 用户类型
     */
    @InEnum(UserTypeEnum.class)
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    /**
     * 社交平台的类型
     */
    @InEnum(SocialTypeEnum.class)
    @NotNull(message = "社交平台的类型不能为空")
    private Integer type;

    /**
     * 社交平台的 unionId
     */
    @NotEmpty(message = "社交平台的 unionId 不能为空")
    private String unionId;

}
