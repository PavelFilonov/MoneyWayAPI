package com.edu.moneywayapi.domain.repository;

import com.edu.moneywayapi.domain.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);

    void deleteById(Long id);

    Optional<Category> findById(Long id);

    boolean existsById(Long id);

    void rename(Long id, String name);

    void saveToUser(Long categoryId, Long userId);

    void saveToGroup(Long categoryId, Long groupId);

    List<Category> findByUser(String username);
}
