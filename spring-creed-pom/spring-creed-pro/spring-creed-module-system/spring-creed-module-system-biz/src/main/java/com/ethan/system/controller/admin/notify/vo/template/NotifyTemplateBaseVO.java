package com.ethan.system.controller.admin.notify.vo.template;

import com.ethan.common.constant.CommonStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
* 站内信模版 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class NotifyTemplateBaseVO {

    @Schema(description = "模版名称", required = true, example = "测试模版")
    @NotEmpty(message = "模版名称不能为空")
    private String name;

    @Schema(description = "模版编码", required = true, example = "SEND_TEST")
    @NotNull(message = "模版编码不能为空")
    private String code;

    @Schema(description = "模版类型 - 对应 system_notify_template_type 字典", required = true, example = "1")
    @NotNull(message = "模版类型不能为空")
    private Integer type;

    @Schema(description = "发送人名称", required = true, example = "土豆")
    @NotEmpty(message = "发送人名称不能为空")
    private String nickname;

    @Schema(description = "模版内容", required = true, example = "我是模版内容")
    @NotEmpty(message = "模版内容不能为空")
    private String content;

    @Schema(description = "状态 - 参见 CommonStatusEnum 枚举", required = true, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "备注", example = "我是备注")
    private String remark;

}
