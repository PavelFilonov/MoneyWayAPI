package com.edu.moneywayapi.domain.repository;

import com.edu.moneywayapi.domain.entity.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    Group save(Group group);

    Optional<Group> findById(Long id);

    void deleteById(Long id);

    Group findByToken(String token);

    void deleteUser(Long groupId, Long userId);

    List<String> getUsers(Long groupId);

    Long getOwnerId(Long groupId);

    void addUser(Long groupId, Long userId);

    void rename(Long groupId, String name);
}
