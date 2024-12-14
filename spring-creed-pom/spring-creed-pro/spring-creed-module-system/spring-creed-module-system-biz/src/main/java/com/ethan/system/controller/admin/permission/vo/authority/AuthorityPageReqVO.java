package com.ethan.system.controller.admin.permission.vo.authority;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.ethan.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;


@Schema(description = "管理后台 - 权限分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorityPageReqVO extends PageParam {

    @Schema(description = "权限代码，模糊匹配", example = "system:admin:operate")
    private String authority;

    @Schema(description = "权限名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "管理员")
    private String name;

    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "1")
    private CommonStatusEnum enabled;

    @Schema(description = "创建时间", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
