package com.ethan.system.controller.admin.tenant.vo.packages;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Tag(name = "管理后台 - 租户套餐更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TenantPackageUpdateReqVO extends TenantPackageBaseVO {

    @Schema(name = "套餐编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "套餐编号不能为空")
    private Long id;

}
