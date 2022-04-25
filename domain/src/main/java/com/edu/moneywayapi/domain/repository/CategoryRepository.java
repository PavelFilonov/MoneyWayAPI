package com.edu.moneywayapi.domain.repository;

import com.edu.moneywayapi.domain.entity.Category;

import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    void deleteById(Long id);
    Optional<Category> findById(Long id);
}
