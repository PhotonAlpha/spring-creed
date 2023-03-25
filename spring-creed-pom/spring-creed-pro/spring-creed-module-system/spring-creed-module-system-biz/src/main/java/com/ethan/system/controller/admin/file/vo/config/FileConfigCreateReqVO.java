package com.ethan.system.controller.admin.file.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Schema(name="管理后台 - 文件配置创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FileConfigCreateReqVO extends FileConfigBaseVO {

    @Schema(name = "存储器", requiredMode = Schema.RequiredMode.REQUIRED, example = "1", description = "参见 FileStorageEnum 枚举类")
    @NotNull(message = "存储器不能为空")
    private Integer storage;

    @Schema(name = "存储配置", requiredMode = Schema.RequiredMode.REQUIRED, description = "配置是动态参数，所以使用 Map 接收")
    @NotNull(message = "存储配置不能为空")
    private Map<String, Object> config;

}
