package com.edu.moneywayapi.domain.service;

import com.edu.moneywayapi.domain.entity.Group;

public interface GroupService {
    Group save(Group group);

    Group findById(Long id);

    void deleteById(Long id);

    Group findByToken(String token);

    void deleteUser(Long groupId, String userLogin);
}
