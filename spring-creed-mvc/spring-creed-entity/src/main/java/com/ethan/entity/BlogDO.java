package com.ethan.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 日志表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "ethan_blogs")
public class BlogDO extends BaseDO {
  private static final long serialVersionUID = -885553981007574579L;

  // 博文ID
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "blog_id", length = 20)
  private Long blogId;

  //发表用户ID
  @Column(name = "blog_b_id", length = 20)
  @NotNull(message = "bloggerId can not empty")
  private Long bloggerId;

  //博文标题
  @Column(name = "blog_title")
  private String bloggerTitle;

  //博文内容
  @Column(name = "blog_content")
  private String blogContent;

  //浏览量
  @Column(name = "blog_views")
  private Long blogViews;

  //评论总数
  @Column(name = "blog_comment_count")
  private Long blogCommentCount;

  //点赞数
  @Column(name = "blog_likes")
  private Long blogLikes;

  //发表日期
  @Column(name = "blog_publish_time")
  private Date blogPublishTime;

  @ManyToMany(mappedBy = "blogList", fetch = FetchType.LAZY)
  private List<CategoryDO> categoryList;

  @ManyToMany(mappedBy = "blogList", fetch = FetchType.LAZY)
  private List<LabelDO> labelList;

  //SnowflakeIdGenerator
}
