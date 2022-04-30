package com.edu.moneywayapi.domain.repository;

import com.edu.moneywayapi.domain.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);

    User findByLogin(String login);

    User findByEmail(String email);

    User save(User user);

    boolean existsById(Long id);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    void deleteById(Long id);

    void updateEmail(String email, Long id);

    void updateLogin(String login, Long id);

    void updatePassword(String password, Long id);
}
