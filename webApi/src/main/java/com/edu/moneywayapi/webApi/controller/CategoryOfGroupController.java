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
import com.edu.moneywayapi.webApi.dto.UserDTO;
import com.edu.moneywayapi.webApi.mapper.CategoryDTOMapper;
import com.edu.moneywayapi.webApi.validator.CategoryValidator;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/categories")
@Slf4j
@Api(description = "Маршруты для категорий групп",
        tags = {"Category"})
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

    @ApiOperation(value = "Получение списка категорий группы", tags = {"Category"})
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Группа не найдена. Возвращается информация об ошибке."),
            @ApiResponse(code = 403, message = "Нет доступа к группе"),
            @ApiResponse(code = 200, message = "Категории получены. Возвращается список категорий."),
            @ApiResponse(code = 404, message = "Категории не найдены")})
    @GetMapping("/groups/{groupId}")
    public ResponseEntity<?> get(@ApiParam("Пользователь") @RequestBody UserDTO principal,
                                 @ApiParam("Id группы") @PathVariable Long groupId) {
        Group group;
        try {
            group = groupService.findById(groupId);
        } catch (NoSuchGroupException e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        if (!groupService.existsUser(groupId, principal.getLogin())) {
            log.warn(String.format("Нет доступа к get /categories/groups/%s", groupId));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.debug(String.format("Успешное подключение к get /categories/groups/%s", groupId));

        List<CategoryDTO> categories = categoryDTOMapper.mapListToDTO(group.getCategories());
        return categories != null && !categories.isEmpty()
                ? new ResponseEntity<>(categories, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Удаление категории группы", tags = {"Category"})
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Категория не найдена"),
            @ApiResponse(code = 403, message = "Нет доступа к удалению категории"),
            @ApiResponse(code = 200, message = "Категория удалена")})
    @DeleteMapping("/{id}/groups/{groupId}")
    public ResponseEntity<?> delete(@ApiParam("Пользователь") @RequestBody UserDTO principal,
                                    @ApiParam("Id категории") @PathVariable Long id,
                                    @ApiParam("Id группы") @PathVariable Long groupId) {
        if (!groupService.existsCategory(groupId, id)) {
            log.warn(String.format("Категория с id %s не найдена", id));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = userService.findByLogin(principal.getLogin());
        if (!groupService.isOwner(groupId, user.getId())) {
            log.warn(String.format("Нет доступа к delete /categories/%s/groups/%s", id, groupId));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.debug(String.format("Успешное подключение к delete /categories/%s/groups/%s", id, groupId));

        categoryService.deleteById(id);
        log.info(String.format("Категория с id %s успешно удалена", id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Переименование категории группы", tags = {"Category"})
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Категория не найдена"),
            @ApiResponse(code = 403, message = "Нет доступа к переименованию"),
            @ApiResponse(code = 200, message = "Категория переименована")})
    @PutMapping("/{id}/groups/{groupId}")
    public ResponseEntity<?> rename(@ApiParam("Пользователь") @RequestBody UserDTO principal,
                                    @ApiParam("Id категории") @PathVariable Long id,
                                    @ApiParam("Id группы") @PathVariable Long groupId,
                                    @ApiParam("Новое название категории") @RequestParam String name) {
        if (!groupService.existsCategory(groupId, id)) {
            log.warn(String.format("Категория с id %s не найдена", id));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = userService.findByLogin(principal.getLogin());
        if (!groupService.isOwner(groupId, user.getId())) {
            log.warn(String.format("Нет доступа к put /categories/%s/groups/%s", id, groupId));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.debug(String.format("Успешное подключение к put /categories/%s/groups/%s", id, groupId));

        categoryService.rename(id, name);
        log.info(String.format("Категория с id %s успешно переименована", id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Добавление категории группы", tags = {"Category"})
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Группа не найдена"),
            @ApiResponse(code = 403, message = "Нет доступа к добавлению"),
            @ApiResponse(code = 422, message = "Невалидная категория. Возвращается список ошибок валидации"),
            @ApiResponse(code = 201, message = "Категория добавлена")})
    @PostMapping("/groups/{groupId}")
    public ResponseEntity<?> add(@ApiParam("Пользователь") @RequestBody UserDTO principal,
                                 @ApiParam("Id группы") @PathVariable Long groupId,
                                 @ApiParam("Добавляемая категория") @RequestBody CategoryDTO category) {
        if (!groupService.existsById(groupId)) {
            log.warn(String.format("Группа с id %s не найдена", groupId));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = userService.findByLogin(principal.getLogin());
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
