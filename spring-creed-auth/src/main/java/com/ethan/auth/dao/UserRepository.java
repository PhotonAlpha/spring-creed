package com.ethan.auth.dao;


import com.ethan.auth.model.Users;

public interface UserRepository extends BaseRepository<Users> {

    Users findByUsername(String username);
}
