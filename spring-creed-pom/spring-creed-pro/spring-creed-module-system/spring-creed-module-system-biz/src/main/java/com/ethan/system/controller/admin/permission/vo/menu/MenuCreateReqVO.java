package com.ethan.system.controller.admin.permission.vo.menu;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Tag(name="管理后台 - 菜单创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuCreateReqVO extends MenuBaseVO {
}
