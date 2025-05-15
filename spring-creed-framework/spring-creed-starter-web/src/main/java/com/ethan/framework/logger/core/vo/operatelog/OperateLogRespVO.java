package com.ethan.framework.logger.core.vo.operatelog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(name = "管理后台 - 操作日志 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OperateLogRespVO extends OperateLogBaseVO {

    @Schema(name = "日志编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(name = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private String userNickname;

}
