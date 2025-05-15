package com.ethan.system.controller.admin.permission.vo.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

@Tag(name="管理后台 - 赋予角色数据权限 Request VO")
@Data
public class PermissionAssignRoleDataScopeReqVO {

    @Schema(name = "角色编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "角色编号不能为空")
    private Long roleId;

    @Schema(name = "数据范围", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 DataScopeEnum 枚举类")
    @NotNull(message = "数据范围不能为空")
//    TODO 这里要多一个枚举校验
    private Integer dataScope;

    @Schema(name = "部门编号列表", example = "1,3,5", description = "只有范围类型为 DEPT_CUSTOM 时，该字段才需要")
    private Set<Long> dataScopeDeptIds = Collections.emptySet(); // 兜底

}
