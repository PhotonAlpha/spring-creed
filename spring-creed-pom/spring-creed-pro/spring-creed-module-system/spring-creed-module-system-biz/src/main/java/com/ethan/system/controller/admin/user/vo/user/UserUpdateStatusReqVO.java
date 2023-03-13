package com.ethan.system.controller.admin.user.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Schema(name = "管理后台 - 用户更新状态 Request VO")
@Data
public class UserUpdateStatusReqVO {

    @Schema(name = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "角色编号不能为空")
    private String id;

    @Schema(name = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "见 CommonStatusEnum 枚举")
    @NotNull(message = "状态不能为空")
//    @InEnum(value = CommonStatusEnum.class, message = "修改状态必须是 {value}")
    private Integer status;

}
