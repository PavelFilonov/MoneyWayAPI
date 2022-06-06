package com.edu.moneywayapi.domain.service.impl;

import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.entity.Group;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.exception.NoSuchGroupException;
import com.edu.moneywayapi.domain.repository.GroupRepository;
import com.edu.moneywayapi.domain.service.GroupService;
import com.edu.moneywayapi.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    private final UserService userService;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository, UserService userService) {
        this.groupRepository = groupRepository;
        this.userService = userService;
    }

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
    public boolean existsById(Long id) {
        return groupRepository.existsById(id);
    }

    @Override
    public Group findByToken(String token) {
        Group group = groupRepository.findByToken(token);
        if (group == null)
            throw new NoSuchGroupException(String.format("The group with token %s was not found", token));
        return group;
    }

    @Override
    public void deleteUser(Long groupId, String userLogin) {
        User user = userService.findByLogin(userLogin);
        groupRepository.deleteUser(groupId, user.getId());
    }

    @Override
    public boolean existsUser(Long groupId, String userLogin) {
        List<String> logins = getUsers(groupId);

        for (String login : logins) {
            if (login.equals(userLogin))
                return true;
        }

        return false;
    }

    @Override
    public List<String> getUsers(Long groupId) {
        return groupRepository.getUsers(groupId);
    }

    @Override
    public boolean isOwner(Long groupId, Long userId) {
        return Objects.equals(groupRepository.getOwnerId(groupId), userId);
    }

    @Override
    public boolean existsCategory(Long groupId, Long categoryId) {
        Group group = findById(groupId);
        List<Category> categories = group.getCategories();

        for (Category category : categories) {
            if (Objects.equals(category.getId(), categoryId))
                return true;
        }

        return false;
    }

    @Override
    public void addUser(Long groupId, Long userId) {
        groupRepository.addUser(groupId, userId);
    }

    @Override
    public void rename(Long groupId, String name) {
        groupRepository.rename(groupId, name);
    }

    @Override
    public List<Group> findByUser(Long userId) {
        return groupRepository.findByUser(userId);
    }
}