package com.ethan.system.controller.admin.file.vo.file;

import com.ethan.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static com.ethan.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(name="管理后台 - 文件分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FilePageReqVO extends PageParam {

    @Schema(name = "文件路径", example = "yudao", description = "模糊匹配")
    private String path;

    @Schema(name = "文件类型", example = "application/octet-stream", description = "模糊匹配")
    private String type;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(name = "创建时间")
    private Date createTime;

}
