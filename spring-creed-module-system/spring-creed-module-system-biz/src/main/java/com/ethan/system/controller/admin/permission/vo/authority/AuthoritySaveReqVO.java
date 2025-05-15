package com.ethan.system.controller.admin.permission.vo.authority;

import com.mzt.logapi.starter.annotation.DiffLogField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 权限创建/更新 Request VO")
@Data
public class AuthoritySaveReqVO {

    @Schema(description = "权限编号", example = "1")
    private Long id;

    @Schema(description = "权限代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "system:admin:operate")
    @NotBlank(message = "权限代码不能为空")
    @Size(max = 30, message = "权限代码长度不能超过 30 个字符")
    @DiffLogField(name = "权限代码")
    private String authority;

    @Schema(description = "权限名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "管理员")
    @NotBlank(message = "权限名称不能为空")
    @Size(max = 50, message = "权限名称长度不能超过 30 个字符")
    @DiffLogField(name = "权限名称")
    private String name;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "显示顺序不能为空")
    @DiffLogField(name = "显示顺序")
    private Integer sort;

    @Schema(description = "备注", example = "我是一个权限")
    @DiffLogField(name = "备注")
    private String remark;

}
