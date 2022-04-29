package com.edu.moneywayapi.dataAccess.repository.impl;

import com.edu.moneywayapi.dataAccess.dal.CategoryDAL;
import com.edu.moneywayapi.dataAccess.mapper.CategoryDALMapper;
import com.edu.moneywayapi.dataAccess.repository.jpa.JpaCategoryRepository;
import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CategoryRepositoryImpl implements CategoryRepository {

    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;

    @Autowired
    private CategoryDALMapper categoryDALMapper;

    @Override
    public Category save(Category category) {
        return categoryDALMapper.map(jpaCategoryRepository.save(categoryDALMapper.map(category)));
    }

    @Override
    public void deleteById(Long id) {
        jpaCategoryRepository.deleteById(id);
    }

    @Override
    public Optional<Category> findById(Long id) {
        CategoryDAL categoryDAL = jpaCategoryRepository.findById(id).orElse(null);
        return Optional.ofNullable(categoryDALMapper.map(categoryDAL));
    }

    @Override
    public boolean existsById(Long id) {
        return jpaCategoryRepository.existsById(id);
    }

    @Override
    public void rename(Long id, String name) {
        jpaCategoryRepository.rename(id, name);
    }

    @Override
    public void saveToUser(Long categoryId, Long userId) {
        jpaCategoryRepository.saveToUser(categoryId, userId);
    }

    @Override
    public void saveToGroup(Long categoryId, Long groupId) {
        jpaCategoryRepository.saveToGroup(categoryId, groupId);
    }
}
