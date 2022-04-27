package com.edu.moneywayapi.domain.service;

import com.edu.moneywayapi.domain.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    User findById(Long id);

    User findByLogin(String login);

    User findByEmail(String email);

    User save(User user);

    boolean existsById(Long id);

    boolean existsByLogin(String login);

    void deleteById(Long id);

    void update(User user, Long id);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}