package com.ethan.system.controller.admin.permission.vo.authority;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.security.websecurity.constant.AuthorityTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 权限信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AuthorityRespVO {

    @Schema(description = "权限编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("权限序号")
    private Long id;

    @Schema(description = "权限代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "system:menu:create")
    @ExcelProperty("权限代码")
    private String authority;

    @Schema(description = "权限名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "管理员")
    @ExcelProperty("权限名称")
    private String name;

    @Schema(description = "权限备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @ExcelProperty("权限备注")
    private String remark;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("权限排序")
    private Integer sort;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    // @ExcelProperty(value = "权限状态", converter = DictConvert.class)
    // @DictFormat(DictTypeConstants.COMMON_STATUS)
    @JsonProperty("status")
    private CommonStatusEnum enabled;

    @Schema(description = "权限类型，参见 RoleTypeEnum 枚举类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private AuthorityTypeEnum type;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "时间戳格式")
    private LocalDateTime createTime;

}
