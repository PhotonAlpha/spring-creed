package com.ethan.system.controller.admin.user.vo.user;

import com.ethan.common.pojo.PageParam;
import com.ethan.common.utils.date.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Schema(name = "管理后台 - 用户分页 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPageReqVO extends PageParam {

    @Schema(name = "用户账号", example = "yudao", description = "模糊匹配")
    private String username;

    @Schema(name = "手机号码", example = "yudao", description = "模糊匹配")
    private String mobile;

    @Schema(name = "展示状态", example = "1", description = "参见 CommonStatusEnum 枚举类")
    private Integer status;

    @Schema(name = "创建时间", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Date[] createTime;

    @Schema(name = "部门编号", example = "1024", description = "同时筛选子部门")
    private Long deptId;

}
