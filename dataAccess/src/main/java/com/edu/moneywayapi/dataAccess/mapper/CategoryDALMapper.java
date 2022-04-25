package com.edu.moneywayapi.dataAccess.mapper;

import com.edu.moneywayapi.dataAccess.dal.CategoryDAL;
import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.mapper.CategoryMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CategoryDALMapper implements CategoryMapper<CategoryDAL> {

    @Override
    public CategoryDAL map(Category category) {
        if (category == null)
            return null;

        return CategoryDAL.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    @Override
    public Category map(CategoryDAL obj) {
        if (obj == null)
            return null;

        return Category.builder()
                .id(obj.getId())
                .name(obj.getName())
                .build();
    }

    public List<CategoryDAL> mapListToDAL(List<Category> categories) {
        List<CategoryDAL> categoriesDAL = new ArrayList<>();

        for (Category category : categories) {
            categoriesDAL.add(map(category));
        }

        return categoriesDAL;
    }

    public List<Category> mapListToEntity(List<CategoryDAL> categoriesDAL) {
        List<Category> categories = new ArrayList<>();

        for (CategoryDAL categoryDAL : categoriesDAL) {
            categories.add(map(categoryDAL));
        }

        return categories;
    }
}
