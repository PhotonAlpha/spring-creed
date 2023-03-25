package com.ethan.system.controller.admin.permission.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Tag(name="管理后台 - 角色信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoleRespVO extends RoleBaseVO {

    @Schema(name = "角色编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(name = "数据范围", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 DataScopeEnum 枚举类")
    private Integer dataScope;

    @Schema(name = "数据范围(指定部门数组)", example = "1")
    private Set<Long> dataScopeDeptIds;

    @Schema(name = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 CommonStatusEnum 枚举类")
    private Integer status;

    @Schema(name = "角色类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 RoleTypeEnum 枚举类")
    private Integer type;

    @Schema(name = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "时间戳格式")
    private Date createTime;

}
