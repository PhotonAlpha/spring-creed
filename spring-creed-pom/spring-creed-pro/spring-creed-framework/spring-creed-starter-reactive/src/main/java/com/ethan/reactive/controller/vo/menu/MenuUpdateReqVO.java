package com.ethan.reactive.controller.vo.menu;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MenuUpdateReqVO extends MenuBaseVO {

    @NotNull(message = "菜单编号不能为空")
    private Long id;

}
