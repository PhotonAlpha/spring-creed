package com.ethan.app.dto;

import com.ethan.entity.CategoryDO;
import com.ethan.entity.LabelDO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@ApiModel("博客实体")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlogDTO {
  private Long blogId;

  //发表用户ID
  @ApiModelProperty("发表用户ID")
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
