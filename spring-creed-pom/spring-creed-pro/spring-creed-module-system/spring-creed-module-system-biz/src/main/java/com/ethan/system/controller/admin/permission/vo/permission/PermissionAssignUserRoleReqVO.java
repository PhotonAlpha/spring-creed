package com.ethan.system.controller.admin.permission.vo.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

@Tag(name="管理后台 - 赋予用户角色 Request VO")
@Data
public class PermissionAssignUserRoleReqVO {

    @Schema(name = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用户编号不能为空")
    private String userId;

    @Schema(name = "角色编号列表", example = "1,3,5")
    private Set<String> roleIds = Collections.emptySet(); // 兜底

}
