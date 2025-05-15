package com.ethan.system.controller.admin.oauth2.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Schema(name="管理后台 - OAuth2 更新用户基本信息 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2UserUpdateReqVO {

    @Schema(name = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @Size(max = 30, message = "用户昵称长度不能超过 30 个字符")
    private String nickname;

    @Schema(name = "用户邮箱", example = "yudao@iocoder.cn")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    private String email;

    @Schema(name = "手机号码", example = "15601691300")
    @Length(min = 11, max = 11, message = "手机号长度必须 11 位")
    private String mobile;

    @Schema(name = "用户性别", example = "1", description = "参见 SexEnum 枚举类")
    private Integer sex;

}
