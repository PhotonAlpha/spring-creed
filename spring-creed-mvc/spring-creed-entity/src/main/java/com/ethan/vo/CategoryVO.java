package com.ethan.vo;

import com.ethan.entity.BaseDO;
import com.ethan.entity.BlogDO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 博客分类表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryVO extends BaseDO {
  private Long categoryId;

  private Long categoryParentId;

  private String categoryName;

  private String categoryAlias;

  private String categoryDescription;


  /**
   * https://stackoverflow.com/questions/15359306/how-to-load-lazy-fetched-items-from-hibernate-jpa-in-my-controller
   */
  private List<BlogDO> blogList;
}
