package com.edu.moneywayapi.domain.repository;

import com.edu.moneywayapi.domain.entity.User;

public interface UserRepository {
    User findByLogin(String login);
    User save(User user);
    boolean existsByLogin(String login);
}
