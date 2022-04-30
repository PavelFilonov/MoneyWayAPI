package com.edu.moneywayapi.dataAccess.repository.impl;

import com.edu.moneywayapi.dataAccess.dal.UserDAL;
import com.edu.moneywayapi.dataAccess.mapper.UserDALMapper;
import com.edu.moneywayapi.dataAccess.repository.jpa.JpaUserRepository;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final UserDALMapper userDALMapper;

    @Autowired
    public UserRepositoryImpl(JpaUserRepository jpaUserRepository, UserDALMapper userDALMapper) {
        this.jpaUserRepository = jpaUserRepository;
        this.userDALMapper = userDALMapper;
    }

    @Override
    public Optional<User> findById(Long id) {
        UserDAL user = jpaUserRepository.findById(id).orElse(null);
        return Optional.ofNullable(userDALMapper.map(user));
    }

    @Override
    public User findByLogin(String login) {
        return userDALMapper.map(jpaUserRepository.findByLogin(login));
    }

    @Override
    public User findByEmail(String email) {
        return userDALMapper.map(jpaUserRepository.findByEmail(email));
    }

    @Override
    public User save(User user) {
        return userDALMapper.map(jpaUserRepository.save(userDALMapper.map(user)));
    }

    @Override
    public boolean existsById(Long id) {
        return jpaUserRepository.existsById(id);
    }

    @Override
    public boolean existsByLogin(String login) {
        return jpaUserRepository.existsByLogin(login);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(Long id) {
        jpaUserRepository.deleteById(id);
    }

    @Override
    public void updateEmail(String email, Long id) {
        jpaUserRepository.updateEmail(email, id);
    }

    @Override
    public void updateLogin(String login, Long id) {
        jpaUserRepository.updateLogin(login, id);
    }

    @Override
    public void updatePassword(String password, Long id) {
        jpaUserRepository.updatePassword(password, id);
    }
}
