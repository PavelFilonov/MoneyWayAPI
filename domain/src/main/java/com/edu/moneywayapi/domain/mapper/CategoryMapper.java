package com.edu.moneywayapi.domain.mapper;

import com.edu.moneywayapi.domain.entity.Category;

public interface CategoryMapper <T> {
    T map(Category category);
    Category map(T obj);
}
