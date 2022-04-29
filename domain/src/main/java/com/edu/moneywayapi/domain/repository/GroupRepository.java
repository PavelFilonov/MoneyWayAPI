package com.edu.moneywayapi.domain.repository;

import com.edu.moneywayapi.domain.entity.Group;

import java.util.Optional;

public interface GroupRepository {
    Group save(Group group);

    Optional<Group> findById(Long id);

    void deleteById(Long id);

    Group findByToken(String token);

    void deleteUser(Long groupId, Long userId);
}
