package com.edu.moneywayapi.domain.service;

import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.exception.NoSuchUserException;
import com.edu.moneywayapi.domain.repository.UserRepository;
import com.edu.moneywayapi.domain.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryService categoryService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("email").login("login").password("password").build();
        Category category = Category.builder().id(1L).name("Транспорт").build();
        user.setCategories(Collections.singletonList(category));
        lenient().when(userRepository.findByLogin("login")).thenReturn(user);

        userService = new UserServiceImpl(userRepository, categoryService);
    }

    @Test
    void findById() {
        // setup
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // test execution
        User userById = userService.findById(1L);

        // test check
        assertEquals(user, userById);
        verify(userRepository).findById(1L);
    }

    @Test
    void notFoundById() {
        // setup
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // test execution and check
        assertThatThrownBy(() -> userService.findById(2L))
                .isExactlyInstanceOf(NoSuchUserException.class);
    }

    @Test
    void findByLogin() {
        // test execution
        User userByLogin = userService.findByLogin("login");

        // test check
        assertEquals(user, userByLogin);
        verify(userRepository).findByLogin("login");
    }

    @Test
    void findByEmail() {
        // setup
        when(userRepository.findByEmail("email")).thenReturn(user);

        // test execution
        User userByEmail = userService.findByEmail("email");

        // test check
        assertEquals(user, userByEmail);
        verify(userRepository).findByEmail("email");
    }

    @Test
    void save() {
        // setup
        when(userRepository.save(user)).thenReturn(user);

        // test execution
        User savedUser = userService.save(user);

        // test check
        assertEquals(user, savedUser);
        verify(userRepository).save(user);
    }

    @Test
    void existsById() {
        // setup
        when(userRepository.existsById(1L)).thenReturn(true);

        // test execution
        boolean exists = userService.existsById(1L);

        // test check
        assertTrue(exists);
        verify(userRepository).existsById(1L);
    }

    @Test
    void notExistsById() {
        // setup
        when(userRepository.existsById(2L)).thenReturn(false);

        // test execution
        boolean exists = userService.existsById(2L);

        // test check
        assertFalse(exists);
        verify(userRepository).existsById(2L);
    }

    @Test
    void existsByLogin() {
        // setup
        when(userRepository.existsByLogin("login")).thenReturn(true);

        // test execution
        boolean exists = userService.existsByLogin("login");

        // test check
        assertTrue(exists);
        verify(userRepository).existsByLogin("login");
    }

    @Test
    void notExistsByLogin() {
        // setup
        when(userRepository.existsByLogin("Wrong login")).thenReturn(false);

        // test execution
        boolean exists = userService.existsByLogin("Wrong login");

        // test check
        assertFalse(exists);
        verify(userRepository).existsByLogin("Wrong login");
    }

    @Test
    void existsByEmail() {
        // setup
        when(userRepository.existsByEmail("email")).thenReturn(true);

        // test execution
        boolean exists = userService.existsByEmail("email");

        // test check
        assertTrue(exists);
        verify(userRepository).existsByEmail("email");
    }

    @Test
    void notExistsByEmail() {
        // setup
        when(userRepository.existsByEmail("Wrong email")).thenReturn(false);

        // test execution
        boolean exists = userService.existsByEmail("Wrong email");

        // test check
        assertFalse(exists);
        verify(userRepository).existsByEmail("Wrong email");
    }

    @Test
    void existsCategory() {
        // setup
        when(categoryService.findByUser("login")).thenReturn(user.getCategories());

        // test execution
        boolean existsCategory = userService.existsCategory("login", 1L);

        // test check
        assertTrue(existsCategory);
        verify(categoryService).findByUser("login");
    }

    @Test
    void notExistsCategory() {
        // setup
        when(categoryService.findByUser("login")).thenReturn(user.getCategories());

        // test execution
        boolean existsCategory = userService.existsCategory("login", 2L);

        // test check
        assertFalse(existsCategory);
        verify(categoryService).findByUser("login");
    }

    @Test
    void deleteById() {
        // test execution
        userService.deleteById(1L);

        // test check
        verify(userRepository).deleteById(1L);
    }

    @Test
    void updateEmail() {
        // test execution
        userService.updateEmail("email", 1L);

        // test check
        verify(userRepository).updateEmail("email", 1L);
    }

    @Test
    void updateLogin() {
        // test execution
        userService.updateLogin("login", 1L);

        // test check
        verify(userRepository).updateLogin("login", 1L);
    }

    @Test
    void updatePassword() {
        // test execution
        userService.updatePassword("password", 1L);

        // test check
        verify(userRepository).updatePassword("password", 1L);
    }

    @Test
    void loadUserByUsernameWhenUserIsNotNull() {
        // setup
        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                Collections.emptyList()
        );

        // test execution
        UserDetails loadedUserDetails = userService.loadUserByUsername("login");

        // test check
        assertEquals(userDetails, loadedUserDetails);
        verify(userRepository).findByLogin("login");
    }

    @Test
    void throwsExceptionWhenUserIsNull() {
        // setup
        when(userRepository.findByLogin(null)).thenReturn(null);

        // test execution and check
        assertThatThrownBy(() -> userService.loadUserByUsername(null))
                .isExactlyInstanceOf(UsernameNotFoundException.class);
    }
}