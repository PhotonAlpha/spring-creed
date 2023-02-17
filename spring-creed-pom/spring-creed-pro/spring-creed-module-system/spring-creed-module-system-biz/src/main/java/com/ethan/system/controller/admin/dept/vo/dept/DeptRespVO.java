package com.ethan.system.controller.admin.dept.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Schema(name="管理后台 - 部门信息 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeptRespVO extends DeptBaseVO {

    @Schema(name = "部门编号", required = true, example = "1024")
    private Long id;

    @Schema(name = "状态", required = true, example = "1", description = "参见 CommonStatusEnum 枚举类")
    private Integer status;

    @Schema(name = "创建时间", required = true, example = "时间戳格式")
    private Date createTime;

}
