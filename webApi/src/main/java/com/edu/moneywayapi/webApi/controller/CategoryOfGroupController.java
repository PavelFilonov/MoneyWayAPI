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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/categories")
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
    public ResponseEntity<?> getByGroup(Principal principal, @PathVariable Long id) {
        if (!groupService.existsUser(id, principal.getName()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Group group;
        try {
            group = groupService.findById(id);
        } catch (NoSuchGroupException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        List<CategoryDTO> categories = categoryDTOMapper.mapListToDTO(group.getCategories());
        return categories != null && !categories.isEmpty()
                ? new ResponseEntity<>(categories, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("//{id}/groups/{groupId}")
    public ResponseEntity<?> deleteFromGroup(Principal principal, @PathVariable Long id, @PathVariable Long groupId) {
        if (!groupService.existsCategory(groupId, id))
            return new ResponseEntity<>("Категория с id " + id + " не найдена", HttpStatus.NOT_FOUND);

        User user = userService.findByLogin(principal.getName());
        if (!groupService.isOwner(groupId, user.getId()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        categoryService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/groups/{groupId}")
    public ResponseEntity<?> renameFromGroup(Principal principal, @PathVariable Long id, @PathVariable Long groupId,
                                             @RequestParam String name) {
        if (!groupService.existsCategory(groupId, id))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        User user = userService.findByLogin(principal.getName());
        if (!groupService.isOwner(groupId, user.getId()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        categoryService.rename(id, name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/groups/{groupId}")
    public ResponseEntity<?> addToGroup(Principal principal, @PathVariable Long groupId,
                                        @RequestBody CategoryDTO category) {
        if (!groupService.existsById(groupId))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        User user = userService.findByLogin(principal.getName());
        if (!groupService.isOwner(groupId, user.getId()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        ValidationResult validationResult = categoryValidator.validate(category);
        if (!validationResult.isValid())
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);

        Category savedCategory = categoryService.save(categoryDTOMapper.map(category));
        categoryService.saveToGroup(savedCategory.getId(), groupId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
