package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.exception.NoSuchCategoryException;
import com.edu.moneywayapi.domain.service.CategoryService;
import com.edu.moneywayapi.webApi.dto.CategoryDTO;
import com.edu.moneywayapi.webApi.mapper.CategoryDTOMapper;
import com.edu.moneywayapi.webApi.validator.CategoryValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryValidator categoryValidator;

    @Autowired
    private CategoryDTOMapper categoryDTOMapper;

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") Long id) {
        try {
            CategoryDTO category = categoryDTOMapper.map(categoryService.findById(id));
            return new ResponseEntity<>(category, HttpStatus.OK);
        } catch (NoSuchCategoryException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
        try {
            categoryService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchCategoryException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody CategoryDTO category) {
        ValidationResult validationResult = categoryValidator.validate(category);
        if (!validationResult.isValid())
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);

        categoryService.save(categoryDTOMapper.map(category));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
