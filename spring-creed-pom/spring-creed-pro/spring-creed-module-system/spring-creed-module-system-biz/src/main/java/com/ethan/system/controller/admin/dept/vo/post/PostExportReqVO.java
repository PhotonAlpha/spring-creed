package com.ethan.system.controller.admin.dept.vo.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name= "管理后台 - 岗位导出 Request VO", description = "参数和 PostExcelVO 是一致的")
@Data
public class PostExportReqVO {

    @Schema(name = "岗位编码", example = "yudao", description = "模糊匹配")
    private String code;

    @Schema(name = "岗位名称", example = "芋道", description = "模糊匹配")
    private String name;

    @Schema(name = "展示状态", example = "1", description = "参见 CommonStatusEnum 枚举类")
    private Integer status;

}
