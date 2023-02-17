package com.ethan.system.controller.admin.tenant.vo.tenant;

import com.ethan.common.utils.date.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Tag(name = "管理后台 - 租户 Excel 导出 Request VO", description = "参数和 TenantPageReqVO 是一致的")
@Data
public class TenantExportReqVO {

    @Schema(name = "租户名", example = "芋道")
    private String name;

    @Schema(name = "联系人", example = "芋艿")
    private String contactName;

    @Schema(name = "联系手机", example = "15601691300")
    private String contactMobile;

    @Schema(name = "租户状态（0正常 1停用）", example = "1")
    private Integer status;

    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(name = "创建时间")
    private Date[] createTime;

}
