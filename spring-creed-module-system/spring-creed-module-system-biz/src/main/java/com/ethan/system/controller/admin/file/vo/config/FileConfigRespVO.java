package com.ethan.system.controller.admin.file.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Schema(name="管理后台 - 文件配置 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FileConfigRespVO extends FileConfigBaseVO {

    @Schema(name = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(name = "存储器", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 FileStorageEnum 枚举类")
    @NotNull(message = "存储器不能为空")
    private Integer storage;

    @Schema(name = "是否为主配置", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否为主配置不能为空")
    private Boolean master;

    @Schema(name = "存储配置", required = true)
    private FileClientConfig config;

    @Schema(name = "创建时间", required = true)
    private Date createTime;

}
