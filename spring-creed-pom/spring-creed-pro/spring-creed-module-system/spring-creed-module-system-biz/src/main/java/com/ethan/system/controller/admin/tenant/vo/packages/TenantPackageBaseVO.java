package com.ethan.system.controller.admin.tenant.vo.packages;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
* 租户套餐 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class TenantPackageBaseVO {

    @Schema(name = "套餐名", requiredMode = Schema.RequiredMode.REQUIRED, example = "VIP")
    @NotNull(message = "套餐名不能为空")
    private String name;

    @Schema(name = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 CommonStatusEnum 枚举")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(name = "备注", example = "好")
    private String remark;

    @Schema(name = "关联的菜单编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "关联的菜单编号不能为空")
    private Set<Long> menuIds;

}
