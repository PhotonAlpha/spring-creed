package com.ethan.system.controller.admin.sms.vo.template;

import com.ethan.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static com.ethan.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;


@Schema(name="管理后台 - 短信模板分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SmsTemplatePageReqVO extends PageParam {

    @Schema(name = "短信签名", example = "1")
    private Integer type;

    @Schema(name = "开启状态", example = "1")
    private Integer status;

    @Schema(name = "模板编码", example = "test_01", description = "模糊匹配")
    private String code;

    @Schema(name = "模板内容", example = "你好，{name}。你长的太{like}啦！", description = "模糊匹配")
    private String content;

    @Schema(name = "短信 API 的模板编号", example = "4383920", description = "模糊匹配")
    private String apiTemplateId;

    @Schema(name = "短信渠道编号", example = "10")
    private Long channelId;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(name = "创建时间")
    private Date[] createTime;

}
