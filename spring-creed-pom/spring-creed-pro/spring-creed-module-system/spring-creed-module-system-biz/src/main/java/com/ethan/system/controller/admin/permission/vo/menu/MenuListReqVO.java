package com.ethan.system.controller.admin.permission.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Tag(name="管理后台 - 菜单列表 Request VO")
@Data
public class MenuListReqVO {

    @Schema(name = "菜单名称", example = "TES", description = "模糊匹配")
    private String name;

    @Schema(name = "展示状态", example = "1", description = "参见 CommonStatusEnum 枚举类")
    private Integer status;

}
