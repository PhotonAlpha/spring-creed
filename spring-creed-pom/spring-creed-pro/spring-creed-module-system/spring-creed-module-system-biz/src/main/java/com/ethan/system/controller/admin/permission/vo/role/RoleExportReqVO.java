package com.ethan.system.controller.admin.permission.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static com.ethan.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Tag(name="管理后台 - 角色分页 Request VO")
@Data
public class RoleExportReqVO {

    @Schema(name = "角色名称", example = "芋道", description = "模糊匹配")
    private String name;

    @Schema(name = "角色标识", example = "yudao", description = "模糊匹配")
    private String code;

    @Schema(name = "展示状态", example = "1", description = "参见 CommonStatusEnum 枚举类")
    private Integer status;

    @Schema(name = "开始时间", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Date createTime;

}
