package com.ethan.system.controller.admin.permission.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 菜单 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class MenuBaseVO {

    @Schema(name = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "TES")
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String name;

    @Schema(name = "权限标识", example = "sys:menu:add", description = "仅菜单类型为按钮时，才需要传递")
    @Size(max = 100)
    private String permission;

    @Schema(name = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 MenuTypeEnum 枚举类")
    @NotNull(message = "菜单类型不能为空")
    private Integer type;

    @Schema(name = "显示顺序不能为空", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @Schema(name = "父菜单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "父菜单 ID 不能为空")
    private Long parentId;

    @Schema(name = "路由地址", example = "post", description = "仅菜单类型为菜单或者目录时，才需要传")
    @Size(max = 200, message = "路由地址不能超过200个字符")
    private String path;

    @Schema(name = "菜单图标", example = "/menu/list", description = "仅菜单类型为菜单或者目录时，才需要传")
    private String icon;

    @Schema(name = "组件路径", example = "system/post/index", description = "仅菜单类型为菜单时，才需要传")
    @Size(max = 200, message = "组件路径不能超过255个字符")
    private String component;

    @Schema(name = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "见 CommonStatusEnum 枚举")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(name = "是否可见", example = "false")
    private Boolean visible;

    @Schema(name = "是否缓存", example = "false")
    private Boolean keepAlive;

    @Schema(description = "是否总是显示", example = "false")
    private Boolean alwaysShow;

}
