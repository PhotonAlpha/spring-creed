package com.ethan.system.controller.admin.permission.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Tag(name="管理后台 - 菜单精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuSimpleRespVO {

    @Schema(name = "菜单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(name = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "TES")
    private String name;

    @Schema(name = "父菜单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long parentId;

    @Schema(name = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 MenuTypeEnum 枚举类")
    private Integer type;

}
