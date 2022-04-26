package com.edu.moneywayapi.domain.repository;

import com.edu.moneywayapi.domain.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);

    User findByLogin(String login);

    User save(User user);

    boolean existsById(Long id);

    boolean existsByLogin(String login);
}
