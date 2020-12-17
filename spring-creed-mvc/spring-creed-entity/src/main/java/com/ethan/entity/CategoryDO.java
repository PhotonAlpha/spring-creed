package com.ethan.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * 博客分类表
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "ethan_category")
public class CategoryDO extends BaseDO {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id", length = 20)
  private Long categoryId;

  @Column(name = "category_parent_id", length = 20)
  private Long categoryParentId;

  @Column(name = "category_name")
  private String categoryName;

  @Column(name = "category_alias")
  private String categoryAlias;

  @Column(name = "category_description")
  private String categoryDescription;


  /**
   * https://stackoverflow.com/questions/15359306/how-to-load-lazy-fetched-items-from-hibernate-jpa-in-my-controller
   */
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "ethan_blog_category",
      joinColumns = { @JoinColumn(name = "category_id", referencedColumnName = "category_id") },
      inverseJoinColumns = { @JoinColumn(name = "blog_id", referencedColumnName = "blog_id") })
  private List<BlogDO> blogList;
}
