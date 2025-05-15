package com.ethan.framework.logger.core.vo.loginlog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Schema(name = "管理后台 - 登录日志 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LoginLogRespVO extends LoginLogBaseVO {

    @Schema(name = "日志编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(name = "用户编号", example = "666")
    private Long userId;

    @Schema(name = "用户类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2", description = "参见 UserTypeEnum 枚举")
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    @Schema(name = "登录时间", required = true)
    private Date createTime;

}
