package com.ethan.system.controller.admin.user.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static com.ethan.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(name = "管理后台 - 用户导出 Request VO", description = "参数和 UserPageReqVO 是一致的")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserExportReqVO {

    @Schema(name = "用户账号", example = "yudao", description = "模糊匹配")
    private String username;

    @Schema(name = "手机号码", example = "yudao", description = "模糊匹配")
    private String mobile;

    @Schema(name = "展示状态", example = "1", description = "参见 CommonStatusEnum 枚举类")
    private Integer status;

    @Schema(name = "创建时间", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Date[] createTime;

    @Schema(name = "部门编号", example = "1024", description = "同时筛选子部门")
    private Long deptId;

}
