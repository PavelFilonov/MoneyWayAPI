package com.edu.moneywayapi.webApi.mapper;

import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.mapper.CategoryMapper;
import com.edu.moneywayapi.webApi.dto.CategoryDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CategoryDTOMapper implements CategoryMapper<CategoryDTO> {

    @Override
    public CategoryDTO map(Category category) {
        if (category == null)
            return null;

        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    @Override
    public Category map(CategoryDTO obj) {
        return Category.builder()
                .id(obj.getId())
                .name(obj.getName())
                .build();
    }

    public List<CategoryDTO> mapListToDTO(List<Category> categories) {
        List<CategoryDTO> categoriesDTO = new ArrayList<>();

        for (Category category : categories) {
            categoriesDTO.add(map(category));
        }

        return categoriesDTO;
    }

    public List<Category> mapListToEntity(List<CategoryDTO> categoriesDTO) {
        List<Category> categories = new ArrayList<>();

        for (CategoryDTO categoryDTO : categoriesDTO) {
            categories.add(map(categoryDTO));
        }

        return categories;
    }
}
