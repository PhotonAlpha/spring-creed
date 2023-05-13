package com.ethan.reactive.controller.vo.menu;

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

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String name;

    @Size(max = 100)
    private String permission;

    @NotNull(message = "菜单类型不能为空")
    private Integer type;

    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    @NotNull(message = "父菜单 ID 不能为空")
    private Long parentId;

    @Size(max = 200, message = "路由地址不能超过200个字符")
    private String path;

    private String icon;

    @Size(max = 200, message = "组件路径不能超过255个字符")
    private String component;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private Boolean visible;

    private Boolean keepAlive;

}
