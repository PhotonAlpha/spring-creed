package com.ethan.app.dao;

import com.ethan.entity.BlogDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlogDao extends JpaRepository<BlogDO, Long> {

  @Override
  @Query(" select b from BlogDO b ")
  List<BlogDO> findAll();
}
