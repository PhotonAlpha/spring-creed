package com.ethan.system.controller.admin.dept.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name="管理后台 - 部门列表 Request VO")
@Data
public class DeptListReqVO {

    @Schema(name = "部门名称", example = "芋道", description = "模糊匹配")
    private String name;

    @Schema(name = "展示状态", example = "1", description = "参见 CommonStatusEnum 枚举类")
    private Integer status;

}
