package com.ethan.system.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Tag(name = "管理后台 - 登录用户的菜单信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthMenuRespVO {

    @Schema(name = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "TEST")
    private Long id;

    @Schema(name = "父菜单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long parentId;

    @Schema(name = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "TEST")
    private String name;

    @Schema(name = "路由地址", example = "post", description = "仅菜单类型为菜单或者目录时，才需要传")
    private String path;

    @Schema(name = "组件路径", example = "system/post/index", description = "仅菜单类型为菜单时，才需要传")
    private String component;

    @Schema(name = "菜单图标", example = "/menu/list", description = "仅菜单类型为菜单或者目录时，才需要传")
    private String icon;

    @Schema(name = "是否可见", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean visible;

    @Schema(name = "是否缓存", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean keepAlive;

    @Schema(description = "是否总是显示", example = "false")
    private Boolean alwaysShow;

    /**
     * 子路由
     */
    private List<AuthMenuRespVO> children;

}
