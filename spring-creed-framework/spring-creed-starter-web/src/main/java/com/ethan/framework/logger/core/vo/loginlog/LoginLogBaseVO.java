package com.ethan.framework.logger.core.vo.loginlog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * 登录日志 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class LoginLogBaseVO {

    @Schema(name = "日志类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 LoginLogTypeEnum 枚举类")
    @NotNull(message = "日志类型不能为空")
    private Integer logType;

    @Schema(name = "链路追踪编号", example = "89aca178-a370-411c-ae02-3f0d672be4ab")
    @NotEmpty(message = "链路追踪编号不能为空")
    private String traceId;

    @Schema(name = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @NotBlank(message = "用户账号不能为空")
    @Size(max = 30, message = "用户账号长度不能超过30个字符")
    private String username;

    @Schema(name = "登录结果", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 LoginResultEnum 枚举类")
    @NotNull(message = "登录结果不能为空")
    private Integer result;

    @Schema(name = "用户 IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    @NotEmpty(message = "用户 IP 不能为空")
    private String userIp;

    @Schema(name = "浏览器 UserAgent", example = "Mozilla/5.0")
    private String userAgent;

}
