package com.ethan.auth.dao;


import com.ethan.auth.model.Authoritys;

public interface RoleRepository extends BaseRepository<Authoritys> {
  Authoritys findByName(String roleName);
}
