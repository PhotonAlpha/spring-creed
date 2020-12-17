package com.ethan.app.dao;

import com.ethan.entity.LabelDO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelDao extends JpaRepository<LabelDO, Long> {
}
