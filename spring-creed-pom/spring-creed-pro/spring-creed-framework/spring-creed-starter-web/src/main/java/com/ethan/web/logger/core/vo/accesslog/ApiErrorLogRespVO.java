package com.ethan.web.logger.core.vo.accesslog;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Tag(name = "管理后台 - API 错误日志 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiErrorLogRespVO extends ApiErrorLogBaseVO {

    @Schema(name = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Integer id;

    @Schema(name = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date createTime;

    @Schema(name = "处理时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date processTime;

    @Schema(name = "处理用户编号", example = "233")
    private Integer processUserId;

}
