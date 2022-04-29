package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.exception.NoSuchCategoryException;
import com.edu.moneywayapi.domain.service.CategoryService;
import com.edu.moneywayapi.domain.service.UserService;
import com.edu.moneywayapi.webApi.dto.CategoryDTO;
import com.edu.moneywayapi.webApi.mapper.CategoryDTOMapper;
import com.edu.moneywayapi.webApi.validator.CategoryValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryOfUserController {

    private final CategoryService categoryService;
    private final CategoryValidator categoryValidator;
    private final CategoryDTOMapper categoryDTOMapper;
    private final UserService userService;

    public CategoryOfUserController(CategoryService categoryService, CategoryValidator categoryValidator,
                                    CategoryDTOMapper categoryDTOMapper, UserService userService) {
        this.categoryService = categoryService;
        this.categoryValidator = categoryValidator;
        this.categoryDTOMapper = categoryDTOMapper;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getByUser(Principal principal) {
        User user = userService.findByLogin(principal.getName());

        List<CategoryDTO> categories = categoryDTOMapper.mapListToDTO(user.getCategories());
        return categories != null && !categories.isEmpty()
                ? new ResponseEntity<>(categories, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFromUser(Principal principal, @PathVariable Long id) {
        if (!userService.existsCategory(principal.getName(), id))
            return new ResponseEntity<>("Категория с id " + id + " не найдена", HttpStatus.NOT_FOUND);

        try {
            categoryService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchCategoryException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> renameFromUser(Principal principal, @PathVariable Long id, @RequestParam String name) {
        if (!userService.existsCategory(principal.getName(), id))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        categoryService.rename(id, name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addToUser(Principal principal, @RequestBody CategoryDTO category) {
        ValidationResult validationResult = categoryValidator.validate(category);
        if (!validationResult.isValid())
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);

        Category savedCategory = categoryService.save(categoryDTOMapper.map(category));
        User user = userService.findByLogin(principal.getName());
        categoryService.saveToUser(savedCategory.getId(), user.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
