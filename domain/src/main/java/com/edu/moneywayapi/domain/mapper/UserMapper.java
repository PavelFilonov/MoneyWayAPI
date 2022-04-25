package com.edu.moneywayapi.domain.mapper;

import com.edu.moneywayapi.domain.entity.User;

public interface UserMapper <T> {
    T map(User user);
    User map(T obj);
}