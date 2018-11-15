package com.example.demo.dao;

import com.example.demo.entity.User;

public interface UserDao {
    User selectById(Long id);
}
