package com.ethan.system.controller.admin.permission.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Tag(name="管理后台 - 角色精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleSimpleRespVO {

    @Schema(name = "角色编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(name = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋道")
    private String name;

}
