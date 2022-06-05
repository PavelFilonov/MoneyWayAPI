package com.edu.moneywayapi.domain.service.impl;

import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.entity.Group;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.exception.NoSuchCategoryException;
import com.edu.moneywayapi.domain.repository.CategoryRepository;
import com.edu.moneywayapi.domain.repository.GroupRepository;
import com.edu.moneywayapi.domain.repository.UserRepository;
import com.edu.moneywayapi.domain.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final UserRepository userService;

    private final GroupRepository groupRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, UserRepository userService, GroupRepository groupRepository) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.groupRepository = groupRepository;
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long id, String username) {
        User user = userService.findByLogin(username);
        categoryRepository.deleteUserCategory(id, user.getId());
        categoryRepository.deleteById(id);
    }

    @Override
    public void delete(Long categoryId, Long groupId) {
        Optional<Group> group = groupRepository.findById(groupId);
        if (group.isEmpty())
            return;

        List<Category> categories = group.get().getCategories();
        Category category = findById(categoryId);
        categories.remove(category);
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

    @Override
    public List<Category> findByUser(String username) {
        return categoryRepository.findByUser(username);
    }
}
