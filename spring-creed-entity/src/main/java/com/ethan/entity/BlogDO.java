package com.ethan.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ethan_blogs")
public class BlogDO extends BaseDo {
  private static final long serialVersionUID = -885553981007574579L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "blog_id", length = 20)
  private Long blogId;

  @Column(name = "blog_b_id", length = 20)
  private Long bloggerId;

  @Column(name = "blog_title")
  private String bloggerTitle;

  @Column(name = "blog_content")
  private String blogContent;

  @Column(name = "blog_views")
  private Long blogViews;

  @Column(name = "blog_comment_count")
  private Long blogCommentCount;

  @Column(name = "blog_likes")
  private Long blogLikes;

  @Column(name = "blog_publish_time")
  private Long blogPublishTime;
}
