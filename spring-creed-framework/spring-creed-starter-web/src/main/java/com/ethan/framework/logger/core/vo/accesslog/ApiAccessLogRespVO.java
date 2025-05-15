package com.ethan.framework.logger.core.vo.accesslog;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Tag(name = "管理后台 - API 访问日志 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiAccessLogRespVO extends ApiAccessLogBaseVO {

    @Schema(name = "日志主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(name = "创建时间", required = true)
    private Date createTime;

}
