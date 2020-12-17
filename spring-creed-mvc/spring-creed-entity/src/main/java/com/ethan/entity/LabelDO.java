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

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "ethan_label")
public class LabelDO extends BaseDO {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "label_id", length = 20)
  private Long labelId;

  @Column(name = "label_name", length = 20)
  private Long labelName;

  @Column(name = "label_alias")
  private String labelAlias;

  @Column(name = "label_description")
  private String labelDescription;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "ethan_blog_label",
      joinColumns = { @JoinColumn(name = "label_id", referencedColumnName = "label_id") },
      inverseJoinColumns = { @JoinColumn(name = "blog_id", referencedColumnName = "blog_id") })
  private List<BlogDO> blogList;
}
