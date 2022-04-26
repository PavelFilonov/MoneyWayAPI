package com.edu.moneywayapi.dataAccess.repository.impl;

import com.edu.moneywayapi.dataAccess.dal.GroupDAL;
import com.edu.moneywayapi.dataAccess.mapper.GroupDALMapper;
import com.edu.moneywayapi.dataAccess.repository.jpa.JpaGroupRepository;
import com.edu.moneywayapi.domain.entity.Group;
import com.edu.moneywayapi.domain.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GroupRepositoryImpl implements GroupRepository {

    @Autowired
    private JpaGroupRepository jpaGroupRepository;

    @Autowired
    private GroupDALMapper groupDALMapper;

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
        jpaGroupRepository.deleteById(id);
    }

    @Override
    public Group findByToken(String token) {
        return groupDALMapper.map(jpaGroupRepository.findByToken(token));
    }

    @Override
    public void deleteUser(Long userId) {
        jpaGroupRepository.deleteUser(userId);
    }
}
