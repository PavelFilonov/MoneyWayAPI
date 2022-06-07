package com.edu.moneywayapi.dataAccess.repository.impl;

import com.edu.moneywayapi.dataAccess.dal.GroupDAL;
import com.edu.moneywayapi.dataAccess.mapper.GroupDALMapper;
import com.edu.moneywayapi.dataAccess.repository.jpa.JpaGroupRepository;
import com.edu.moneywayapi.domain.entity.Group;
import com.edu.moneywayapi.domain.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GroupRepositoryImpl implements GroupRepository {

    private final JpaGroupRepository jpaGroupRepository;
    private final GroupDALMapper groupDALMapper;

    @Autowired
    public GroupRepositoryImpl(JpaGroupRepository jpaGroupRepository, GroupDALMapper groupDALMapper) {
        this.jpaGroupRepository = jpaGroupRepository;
        this.groupDALMapper = groupDALMapper;
    }

    @Override
    public Group save(Group group) {
        return groupDALMapper.map(jpaGroupRepository.save(groupDALMapper.map(group)));
    }

    @Override
    public Optional<Group> findById(Long id) {
        GroupDAL groupDAL = jpaGroupRepository.findById(id).orElse(null);
        return Optional.ofNullable(groupDALMapper.map(groupDAL));
    }

    @Override
    public void deleteById(Long id) {
        jpaGroupRepository.deleteUserGroup(id);
        jpaGroupRepository.deleteById(id);
    }

    @Override
    public Group findByToken(String token) {
        return groupDALMapper.map(jpaGroupRepository.findByToken(token));
    }

    @Override
    public void deleteUser(Long groupId, Long userId) {
        jpaGroupRepository.deleteUser(groupId, userId);
    }

    @Override
    public List<String> getUsers(Long groupId) {
        return jpaGroupRepository.getUsers(groupId);
    }

    @Override
    public Long getOwnerId(Long groupId) {
        return jpaGroupRepository.getOwnerId(groupId);
    }

    @Override
    public void addUser(Long groupId, Long userId) {
        jpaGroupRepository.addUser(groupId, userId);
    }

    @Override
    public void rename(Long groupId, String name) {
        jpaGroupRepository.rename(groupId, name);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaGroupRepository.existsById(id);
    }

    @Override
    public List<Group> findByUser(Long userId) {
        return groupDALMapper.mapListToEntity(jpaGroupRepository.findByUser(userId));
    }
}
