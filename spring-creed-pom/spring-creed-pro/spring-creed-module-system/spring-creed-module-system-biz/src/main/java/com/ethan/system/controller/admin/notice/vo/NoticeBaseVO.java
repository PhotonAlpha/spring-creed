package com.ethan.system.controller.admin.notice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 通知公告 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class NoticeBaseVO {

    @Schema(description = "公告标题", required = true, example = "小博主")
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 50, message = "公告标题不能超过50个字符")
    private String title;

    @Schema(description = "公告类型", required = true, example = "小博主")
    @NotNull(message = "公告类型不能为空")
    private Integer type;

    @Schema(description = "公告内容", required = true, example = "半生编码")
    private String content;

    @Schema(description = "状态,参见 CommonStatusEnum 枚举类", required = true, example = "1")
    private Integer status;

}
