package com.edu.moneywayapi.domain.service;

import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.entity.Group;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.exception.NoSuchGroupException;
import com.edu.moneywayapi.domain.repository.GroupRepository;
import com.edu.moneywayapi.domain.service.impl.GroupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    private GroupService groupService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserService userService;

    private Group group;

    @BeforeEach
    void setUp() {
        group = Group.builder().id(1L).name("Семейный бюджет").ownerId(1L).build();
        lenient().when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        lenient().when(groupRepository.getUsers(group.getId())).thenReturn(Collections.singletonList("Admin"));

        groupService = new GroupServiceImpl(groupRepository, userService);
    }

    @Test
    void save() {
        // setup
        when(groupRepository.save(group)).thenReturn(group);

        // test execution
        Group savedGroup = groupService.save(group);

        // test check
        assertEquals(group, savedGroup);
        verify(groupRepository).save(group);
    }

    @Test
    void findById() {
        // test execution
        Group groupById = groupService.findById(1L);

        // test check
        assertEquals(group, groupById);
        verify(groupRepository).findById(1L);
    }

    @Test
    void throwsExceptionWhenGroupNotFoundById() {
        // setup
        when(groupRepository.findById(2L)).thenReturn(Optional.empty());

        // test execution and check
        assertThatThrownBy(() -> groupService.findById(2L))
                .isExactlyInstanceOf(NoSuchGroupException.class);
        verify(groupRepository).findById(2L);
    }

    @Test
    void deleteById() {
        // test execution
        groupService.deleteById(1L);

        // text check
        verify(groupRepository).deleteById(1L);
    }

    @Test
    void existsById() {
        // setup
        when(groupRepository.existsById(1L)).thenReturn(true);

        // test execution
        boolean exists = groupService.existsById(1L);

        // test check
        assertTrue(exists);
        verify(groupRepository).existsById(1L);
    }

    @Test
    void notExistsById() {
        // setup
        when(groupRepository.existsById(2L)).thenReturn(false);

        // test execution
        boolean exists = groupService.existsById(2L);

        // test check
        assertFalse(exists);
        verify(groupRepository).existsById(2L);
    }

    @Test
    void findByToken() {
        // setup
        when(groupRepository.findByToken("Right token")).thenReturn(group);

        // test execution
        Group groupByToken = groupService.findByToken("Right token");

        // test check
        assertEquals(group, groupByToken);
        verify(groupRepository).findByToken("Right token");
    }

    @Test
    void throwsExceptionWhenGroupNotFoundByToken() {
        // setup
        when(groupRepository.findByToken("Wrong token")).thenReturn(null);

        // test execution and check
        assertThatThrownBy(() -> groupService.findByToken("Wrong token"))
                .isExactlyInstanceOf(NoSuchGroupException.class);
        verify(groupRepository).findByToken("Wrong token");
    }

    @Test
    void deleteUser() {
        // setup
        User user = User.builder().id(1L).login("Admin").build();
        when(userService.findByLogin(user.getLogin())).thenReturn(user);
        InOrder inOrder = Mockito.inOrder(userService, groupRepository);

        // test execution
        groupService.deleteUser(group.getId(), user.getLogin());

        // test check
        inOrder.verify(userService).findByLogin(user.getLogin());
        inOrder.verify(groupRepository).deleteUser(group.getId(), user.getId());
    }

    @Test
    void existsUser() {
        // test execution
        boolean exists = groupService.existsUser(group.getId(), "Admin");

        // test check
        assertTrue(exists);
        verify(groupRepository).getUsers(group.getId());
    }

    @Test
    void notExistsUser() {
        // test execution
        boolean exists = groupService.existsUser(group.getId(), "Not admin");

        // test check
        assertFalse(exists);
        verify(groupRepository).getUsers(group.getId());
    }

    @Test
    void getUsers() {
        // test execution
        List<String> users = groupService.getUsers(group.getId());

        // test check
        assertEquals(Collections.singletonList("Admin"), users);
        verify(groupRepository).getUsers(group.getId());
    }

    @Test
    void isOwner() {
        // setup
        when(groupRepository.getOwnerId(1L)).thenReturn(1L);

        // test execution
        boolean isOwner = groupService.isOwner(1L, 1L);

        // test check
        assertTrue(isOwner);
        verify(groupRepository).getOwnerId(1L);
    }

    @Test
    void isNotOwner() {
        // setup
        when(groupRepository.getOwnerId(1L)).thenReturn(1L);

        // test execution
        boolean isOwner = groupService.isOwner(1L, 2L);

        // test check
        assertFalse(isOwner);
        verify(groupRepository).getOwnerId(1L);
    }

    @Test
    void existsCategory() {
        // setup
        Category category = Category.builder().id(1L).build();
        group.setCategories(Collections.singletonList(category));

        // test execution
        boolean existsCategory = groupService.existsCategory(group.getId(), category.getId());

        // test check
        assertTrue(existsCategory);
        verify(groupRepository).findById(group.getId());
    }

    @Test
    void notExistsCategory() {
        // setup
        Category category = Category.builder().id(1L).build();
        group.setCategories(Collections.singletonList(category));

        // test execution
        boolean existsCategory = groupService.existsCategory(group.getId(), 2L);

        // test check
        assertFalse(existsCategory);
        verify(groupRepository).findById(group.getId());
    }

    @Test
    void addUser() {
        // test execution
        groupService.addUser(1L, 1L);

        // test check
        verify(groupRepository).addUser(1L, 1L);
    }

    @Test
    void rename() {
        // test execution
        groupService.rename(1L, "Новое название");

        // test check
        verify(groupRepository).rename(1L, "Новое название");
    }
}