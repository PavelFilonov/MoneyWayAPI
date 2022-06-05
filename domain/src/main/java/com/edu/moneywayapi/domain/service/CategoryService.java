package com.edu.moneywayapi.domain.service;

import com.edu.moneywayapi.domain.entity.Category;

import java.util.List;

public interface CategoryService {
    Category save(Category category);

    void delete(Long id, String username);

    void delete(Long categoryId, Long groupId);

    Category findById(Long id);

    boolean existsById(Long id);

    void rename(Long id, String name);

    void saveToUser(Long categoryId, Long userId);

    void saveToGroup(Long categoryId, Long groupId);

    List<Category> findByUser(String username);
}
