package com.ethan.system.controller.admin.permission.vo.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

@Tag(name="管理后台 - 赋予角色菜单 Request VO")
@Data
public class PermissionAssignRoleMenuReqVO {

    @Schema(name = "角色编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "角色编号不能为空")
    private String roleId;

    @Schema(name = "菜单编号列表", example = "1,3,5")
    private Set<Long> menuIds = Collections.emptySet(); // 兜底

}
