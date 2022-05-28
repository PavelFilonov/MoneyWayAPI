package com.edu.moneywayapi.domain.service;

import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.exception.NoSuchCategoryException;
import com.edu.moneywayapi.domain.repository.CategoryRepository;
import com.edu.moneywayapi.domain.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Транспорт").build();
        categoryService = new CategoryServiceImpl(categoryRepository);
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
    void deleteById() {
        // test execution
        categoryService.deleteById(1L);

        // text check
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void findById() {
        // setup
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

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