package com.ethan.app.dto;

import com.ethan.entity.CategoryDO;
import com.ethan.entity.LabelDO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Schema(name = "博客实体")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlogDTO {
  private Long blogId;

  //发表用户ID
  @Schema(name = "发表用户ID", example = "11111")
  private Long bloggerId;

  //博文标题
  private String bloggerTitle;

  //博文内容
  private String blogContent;

  //浏览量
  private Long blogViews;

  //评论总数
  private Long blogCommentCount;

  //点赞数
  private Long blogLikes;

  //发表日期
  private Date blogPublishTime;

  private List<CategoryDO> categoryList;

  private List<LabelDO> labelList;

}
