package com.ethan.app.dao;

import com.ethan.entity.CategoryDO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDao extends JpaRepository<CategoryDO, Long> {
}
