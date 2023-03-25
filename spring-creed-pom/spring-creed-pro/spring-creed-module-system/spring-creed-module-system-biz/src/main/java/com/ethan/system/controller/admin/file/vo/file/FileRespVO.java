package com.ethan.system.controller.admin.file.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(name = "管理后台 - 文件 Response VO", description = "不返回 content 字段，太大")
@Data
public class FileRespVO {

    @Schema(name = "文件编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(name = "配置编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    private Long configId;

    @Schema(name = "文件路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao.jpg")
    private String path;

    @Schema(name = "原文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao.jpg")
    private String name;

    @Schema(name = "文件 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/yudao.jpg")
    private String url;

    @Schema(name = "文件MIME类型", example = "application/octet-stream")
    private String type;

    @Schema(name = "文件大小", example = "2048", required = true)
    private Integer size;

    @Schema(name = "创建时间", required = true)
    private Date createTime;

}
