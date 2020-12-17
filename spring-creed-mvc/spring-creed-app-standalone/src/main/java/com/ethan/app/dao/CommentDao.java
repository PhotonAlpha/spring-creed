package com.ethan.app.dao;

import com.ethan.entity.CommentDO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentDao extends JpaRepository<CommentDO, Long> {
}
