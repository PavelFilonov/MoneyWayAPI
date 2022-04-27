package com.edu.moneywayapi.domain.service.impl;

import com.edu.moneywayapi.domain.entity.Group;
import com.edu.moneywayapi.domain.exception.NoSuchGroupException;
import com.edu.moneywayapi.domain.repository.GroupRepository;
import com.edu.moneywayapi.domain.service.GroupService;
import com.edu.moneywayapi.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserService userService;

    @Override
    public Group save(Group group) {
        group.setToken(String.valueOf(UUID.randomUUID()));
        return groupRepository.save(group);
    }

    @Override
    public Group findById(Long id) {
        Optional<Group> group = groupRepository.findById(id);
        if (group.isEmpty())
            throw new NoSuchGroupException(String.format("The group with id %s was not found", id));
        return group.get();
    }

    @Override
    public void deleteById(Long id) {
        findById(id);
        groupRepository.deleteById(id);
    }

    @Override
    public Group findByToken(String token) {
        Group group = groupRepository.findByToken(token);
        if (group == null)
            throw new NoSuchGroupException(String.format("The group with token %s was not found", token));
        return group;
    }

    @Override
    public void deleteUser(Long userId) {
        userService.findById(userId);
        groupRepository.deleteUser(userId);
    }
}