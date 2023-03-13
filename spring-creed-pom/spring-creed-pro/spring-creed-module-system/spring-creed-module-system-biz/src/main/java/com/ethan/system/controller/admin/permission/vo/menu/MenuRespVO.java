package com.ethan.system.controller.admin.permission.vo.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Tag(name="管理后台 - 菜单信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MenuRespVO extends MenuBaseVO {

    @Schema(name = "菜单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(name = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 CommonStatusEnum 枚举类")
    private Integer status;

    @Schema(name = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "时间戳格式")
    private Date createTime;

}
