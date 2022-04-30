package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.entity.Group;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.exception.NoSuchGroupException;
import com.edu.moneywayapi.domain.service.CategoryService;
import com.edu.moneywayapi.domain.service.GroupService;
import com.edu.moneywayapi.domain.service.UserService;
import com.edu.moneywayapi.webApi.dto.CategoryDTO;
import com.edu.moneywayapi.webApi.mapper.CategoryDTOMapper;
import com.edu.moneywayapi.webApi.validator.CategoryValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/categories")
@Slf4j
public class CategoryOfGroupController {

    private final CategoryService categoryService;
    private final CategoryValidator categoryValidator;
    private final CategoryDTOMapper categoryDTOMapper;
    private final UserService userService;
    private final GroupService groupService;

    @Autowired
    public CategoryOfGroupController(CategoryService categoryService, CategoryValidator categoryValidator,
                                     CategoryDTOMapper categoryDTOMapper, UserService userService, GroupService groupService) {
        this.categoryService = categoryService;
        this.categoryValidator = categoryValidator;
        this.categoryDTOMapper = categoryDTOMapper;
        this.userService = userService;
        this.groupService = groupService;
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity<?> get(Principal principal, @PathVariable Long id) {
        if (!groupService.existsUser(id, principal.getName())) {
            log.warn(String.format("Нет доступа к get /categories/groups/%s", id));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.debug(String.format("Успешное подключение к get /categories/groups/%s", id));

        Group group;
        try {
            group = groupService.findById(id);
        } catch (NoSuchGroupException e) {
            log.warn(String.format("Не найдена группа с id %s", id));
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        List<CategoryDTO> categories = categoryDTOMapper.mapListToDTO(group.getCategories());
        return categories != null && !categories.isEmpty()
                ? new ResponseEntity<>(categories, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}/groups/{groupId}")
    public ResponseEntity<?> delete(Principal principal, @PathVariable Long id, @PathVariable Long groupId) {
        if (!groupService.existsCategory(groupId, id)) {
            log.warn(String.format("Категория с id %s не найдена", id));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = userService.findByLogin(principal.getName());
        if (!groupService.isOwner(groupId, user.getId())) {
            log.warn(String.format("Нет доступа к delete /categories/%s/groups/%s", id, groupId));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.debug(String.format("Успешное подключение к delete /categories/%s/groups/%s", id, groupId));

        categoryService.deleteById(id);
        log.info(String.format("Категория с id %s успешно удалена", id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/groups/{groupId}")
    public ResponseEntity<?> rename(Principal principal, @PathVariable Long id, @PathVariable Long groupId,
                                    @RequestParam String name) {
        if (!groupService.existsCategory(groupId, id)) {
            log.warn(String.format("Категория с id %s не найдена", id));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = userService.findByLogin(principal.getName());
        if (!groupService.isOwner(groupId, user.getId())) {
            log.warn(String.format("Нет доступа к put /categories/%s/groups/%s", id, groupId));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.debug(String.format("Успешное подключение к put /categories/%s/groups/%s", id, groupId));

        categoryService.rename(id, name);
        log.info(String.format("Категория с id %s успешно переименована", id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/groups/{groupId}")
    public ResponseEntity<?> add(Principal principal, @PathVariable Long groupId, @RequestBody CategoryDTO category) {
        if (!groupService.existsById(groupId)) {
            log.warn(String.format("Группа с id %s не найдена", groupId));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = userService.findByLogin(principal.getName());
        if (!groupService.isOwner(groupId, user.getId())) {
            log.warn(String.format("Нет доступа к post /categories/groups/%s", groupId));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.debug(String.format("Успешное подключение к post /categories/groups/%s", groupId));

        ValidationResult validationResult = categoryValidator.validate(category);
        if (!validationResult.isValid()) {
            log.warn("Невалидная категория: " + validationResult.getErrors());
            return new ResponseEntity<>(validationResult.getErrors(), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Category savedCategory = categoryService.save(categoryDTOMapper.map(category));
        categoryService.saveToGroup(savedCategory.getId(), groupId);
        log.info("Категория успешно добавлена");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
