package com.edu.moneywayapi.domain.service;

import com.edu.moneywayapi.domain.entity.Category;

public interface CategoryService {
    Category save(Category category);

    void deleteById(Long id);

    Category findById(Long id);
}
