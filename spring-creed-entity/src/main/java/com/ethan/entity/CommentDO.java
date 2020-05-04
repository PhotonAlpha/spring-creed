package com.ethan.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 评论表
 */
@Data
@Entity
@Table(name = "ethan_comment")
public class CommentDO extends BaseDO {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "comm_id", length = 20)
  private Long commentId;

  @Column(name = "comm_parent_id", length = 20)
  private Long commentParentId;

  @Column(name = "comm_b_id", length = 20)
  private Long bloggerId;

  @Column(name = "comm_blog_id", length = 20)
  private Long blogId;

  @Column(name = "comm_content", length = 20)
  private String commentContent;

  @Column(name = "comm_time")
  private Date commentTime;

  @Column(name = "comm_likes")
  private Long commentLikes;

}
