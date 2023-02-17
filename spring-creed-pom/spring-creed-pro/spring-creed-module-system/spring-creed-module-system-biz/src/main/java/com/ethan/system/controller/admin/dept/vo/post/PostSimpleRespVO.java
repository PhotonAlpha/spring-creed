package com.ethan.system.controller.admin.dept.vo.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name="管理后台 - 岗位精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSimpleRespVO {

    @Schema(name = "岗位编号", required = true, example = "1024")
    private Long id;

    @Schema(name = "岗位名称", required = true, example = "芋道")
    private String name;

}
