package com.edu.moneywayapi.domain.service;

import com.edu.moneywayapi.domain.entity.Group;

import java.util.List;

public interface GroupService {
    Group save(Group group);

    Group findById(Long id);

    void deleteById(Long id);

    Group findByToken(String token);

    void deleteUser(Long groupId, String userLogin);

    boolean existsUser(Long groupId, String userLogin);

    List<String> getUsers(Long groupId);

    boolean isOwner(Long groupId, Long userId);

    void addUser(Long groupId, Long userId);

    void rename(Long groupId, String name);
}
