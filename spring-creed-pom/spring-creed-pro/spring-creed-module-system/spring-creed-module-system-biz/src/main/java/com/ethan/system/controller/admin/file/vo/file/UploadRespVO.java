package com.ethan.system.controller.admin.file.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "管理后台 - 上传文件 VO")
public class UploadRespVO {

    @Schema(name = "文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao.jpg")
    private String fileName;

    @Schema(name = "文件 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/yudao.jpg")
    private String fileUrl;
}
