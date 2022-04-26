package com.edu.moneywayapi.domain.service;

import com.edu.moneywayapi.domain.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    User findByLogin(String login);
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
