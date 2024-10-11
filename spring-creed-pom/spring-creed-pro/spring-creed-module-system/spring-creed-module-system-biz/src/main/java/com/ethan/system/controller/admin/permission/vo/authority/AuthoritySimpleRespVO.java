package com.ethan.system.controller.admin.permission.vo.authority;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 权限精简信息 Response VO")
@Data
public class AuthoritySimpleRespVO {

    @Schema(description = "权限编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "权限代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "system:admin:operate")
    private String authority;

    @Schema(description = "权限名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "管理员")
    private String name;

}
