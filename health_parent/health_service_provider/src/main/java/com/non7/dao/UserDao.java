package com.non7.dao;


import com.non7.pojo.User;

public interface UserDao {

    public User findByUserName(String username);
}
