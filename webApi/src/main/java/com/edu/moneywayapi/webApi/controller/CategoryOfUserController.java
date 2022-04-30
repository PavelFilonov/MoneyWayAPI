package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.service.CategoryService;
import com.edu.moneywayapi.domain.service.UserService;
import com.edu.moneywayapi.webApi.dto.CategoryDTO;
import com.edu.moneywayapi.webApi.mapper.CategoryDTOMapper;
import com.edu.moneywayapi.webApi.validator.CategoryValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/categories")
@Slf4j
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
    public ResponseEntity<?> get(Principal principal) {
        log.debug("Успешное подключение к get /categories");

        User user = userService.findByLogin(principal.getName());
        List<CategoryDTO> categories = categoryDTOMapper.mapListToDTO(user.getCategories());
        return categories != null && !categories.isEmpty()
                ? new ResponseEntity<>(categories, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Principal principal, @PathVariable Long id) {
        log.debug(String.format("Успешное подключение к delete /categories/%s", id));

        if (!userService.existsCategory(principal.getName(), id)) {
            log.warn(String.format("Категория с id %s не найдена", id));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        categoryService.deleteById(id);
        log.info(String.format("Категория с id %s успешно удалена", id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> rename(Principal principal, @PathVariable Long id, @RequestParam String name) {
        log.debug(String.format("Успешное подключение к put /categories/%s", id));

        if (!userService.existsCategory(principal.getName(), id)) {
            log.warn(String.format("Категория с id %s не найдена", id));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        categoryService.rename(id, name);
        log.info(String.format("Категория с id %s успешно переименована", id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> add(Principal principal, @RequestBody CategoryDTO category) {
        log.debug("Успешное подключение к post /categories");

        ValidationResult validationResult = categoryValidator.validate(category);
        if (!validationResult.isValid()) {
            log.warn("Невалидная категория: " + validationResult.getErrors());
            return new ResponseEntity<>(validationResult.getErrors(), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Category savedCategory = categoryService.save(categoryDTOMapper.map(category));
        User user = userService.findByLogin(principal.getName());
        categoryService.saveToUser(savedCategory.getId(), user.getId());
        log.info("Категория успешно добавлена");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
