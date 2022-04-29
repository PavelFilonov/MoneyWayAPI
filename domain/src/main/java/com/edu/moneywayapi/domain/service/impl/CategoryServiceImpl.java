package com.edu.moneywayapi.domain.service.impl;

import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.exception.NoSuchCategoryException;
import com.edu.moneywayapi.domain.repository.CategoryRepository;
import com.edu.moneywayapi.domain.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Category findById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty())
            throw new NoSuchCategoryException(String.format("The category with id %s was not found", id));
        return category.get();
    }

    @Override
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public void rename(Long id, String name) {
        categoryRepository.rename(id, name);
    }

    @Override
    public void saveToUser(Long categoryId, Long userId) {
        categoryRepository.saveToUser(categoryId, userId);
    }

    @Override
    public void saveToGroup(Long categoryId, Long groupId) {
        categoryRepository.saveToGroup(categoryId, groupId);
    }
}
