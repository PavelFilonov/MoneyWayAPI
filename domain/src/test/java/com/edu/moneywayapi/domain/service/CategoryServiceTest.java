package com.edu.moneywayapi.domain.service;

import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.entity.Group;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.exception.NoSuchCategoryException;
import com.edu.moneywayapi.domain.repository.CategoryRepository;
import com.edu.moneywayapi.domain.repository.GroupRepository;
import com.edu.moneywayapi.domain.repository.UserRepository;
import com.edu.moneywayapi.domain.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    private Category category;

    private List<Category> categories;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Транспорт").build();
        categories = new ArrayList<>();
        categories.add(category);
        lenient().when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService = new CategoryServiceImpl(categoryRepository, userRepository, groupRepository);
    }

    @Test
    void save() {
        // setup
        when(categoryRepository.save(category)).thenReturn(category);

        // test execution
        Category savedCategory = categoryService.save(category);

        // test check
        assertEquals(category, savedCategory);
        verify(categoryRepository).save(category);
    }

    @Test
    void deleteForUser() {
        // setup
        User user = User.builder().id(1L).login("login").categories(categories).build();
        when(userRepository.findByLogin(user.getLogin())).thenReturn(user);
        InOrder inOrder = inOrder(userRepository, categoryRepository);

        // test execution
        categoryService.delete(1L, "login");

        // text check
        inOrder.verify(userRepository).findByLogin("login");
        inOrder.verify(categoryRepository).deleteUserCategory(1L, 1L);
        inOrder.verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteForGroup() {
        // setup
        Optional<Group> group = Optional.ofNullable(Group.builder().id(1L).categories(categories).build());
        when(groupRepository.findById(group.get().getId())).thenReturn(group);
        InOrder inOrder = inOrder(groupRepository, categoryRepository);

        // test execution
        categoryService.delete(1L, 1L);

        // text check
        inOrder.verify(groupRepository).findById(1L);
        inOrder.verify(categoryRepository).findById(1L);
    }

    @Test
    void findById() {
        // test execution
        Category categoryById = categoryService.findById(1L);

        // test check
        assertEquals(category, categoryById);
        verify(categoryRepository).findById(1L);
    }

    @Test
    void throwsExceptionWhenCategoryNotFoundById() {
        // setup
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        // test execution and check
        assertThatThrownBy(() -> categoryService.findById(2L))
                .isExactlyInstanceOf(NoSuchCategoryException.class);
        verify(categoryRepository).findById(2L);
    }

    @Test
    void existsById() {
        // setup
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // test execution
        boolean exists = categoryService.existsById(1L);

        // test check
        assertTrue(exists);
        verify(categoryRepository).existsById(1L);
    }

    @Test
    void notExistsById() {
        // setup
        when(categoryRepository.existsById(2L)).thenReturn(false);

        // test execution
        boolean exists = categoryService.existsById(2L);

        // test check
        assertFalse(exists);
        verify(categoryRepository).existsById(2L);
    }

    @Test
    void rename() {
        // test execution
        categoryService.rename(1L, "Кафе");

        // test check
        verify(categoryRepository).rename(1L, "Кафе");
    }

    @Test
    void saveToUser() {
        // test execution
        categoryService.saveToUser(1L, 1L);

        // test check
        verify(categoryRepository).saveToUser(1L, 1L);
    }

    @Test
    void saveToGroup() {
        // test execution
        categoryService.saveToGroup(1L, 1L);

        // test check
        verify(categoryRepository).saveToGroup(1L, 1L);
    }
}