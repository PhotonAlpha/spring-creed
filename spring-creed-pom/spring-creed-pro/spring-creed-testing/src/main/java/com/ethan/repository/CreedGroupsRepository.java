package com.ethan.repository;

import com.ethan.entity.CreedGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreedGroupsRepository extends JpaRepository<CreedGroups, String> {
}
