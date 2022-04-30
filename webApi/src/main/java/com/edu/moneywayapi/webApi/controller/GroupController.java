package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.exception.NoSuchGroupException;
import com.edu.moneywayapi.domain.service.GroupService;
import com.edu.moneywayapi.domain.service.UserService;
import com.edu.moneywayapi.webApi.dto.GroupDTO;
import com.edu.moneywayapi.webApi.mapper.GroupDTOMapper;
import com.edu.moneywayapi.webApi.validator.GroupValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/groups")
@Slf4j
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;
    private final GroupValidator groupValidator;
    private final GroupDTOMapper groupDTOMapper;

    @Autowired
    public GroupController(GroupService groupService, UserService userService,
                           GroupValidator groupValidator, GroupDTOMapper groupDTOMapper) {
        this.groupService = groupService;
        this.userService = userService;
        this.groupValidator = groupValidator;
        this.groupDTOMapper = groupDTOMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(Principal principal, @PathVariable(name = "id") Long id) {
        log.debug(String.format("Успешное подключение к get /groups/%s", id));

        if (!groupService.existsUser(id, principal.getName()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try {
            GroupDTO group = groupDTOMapper.map(groupService.findById(id));
            return new ResponseEntity<>(group, HttpStatus.OK);
        } catch (NoSuchGroupException e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getByToken(@RequestParam String token) {
        log.debug("Успешное подключение к get /groups");

        try {
            GroupDTO group = groupDTOMapper.map(groupService.findByToken(token));
            return new ResponseEntity<>(group, HttpStatus.OK);
        } catch (NoSuchGroupException e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody GroupDTO group) {
        log.debug("Успешное подключение к post /groups");

        ValidationResult validationResult = groupValidator.validate(group);
        if (!validationResult.isValid()) {
            log.warn("Невалидная группа: " + validationResult.getErrors());
            return new ResponseEntity<>(validationResult.getErrors(), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        groupService.save(groupDTOMapper.map(group));
        log.info("Группа добавлена");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(Principal principal, @PathVariable(name = "id") Long id) {
        User user = userService.findByLogin(principal.getName());
        if (!groupService.isOwner(id, user.getId())) {
            log.warn(String.format("Нет доступа к delete /groups/%s", id));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.debug(String.format("Успешное подключение к delete /groups/%s", id));

        try {
            groupService.deleteById(id);
            log.info(String.format("Группа с id %s удалена", id));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchGroupException e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}/users")
    public ResponseEntity<?> deleteUser(Principal principal, @PathVariable Long id, @RequestParam String userLogin) {
        User user = userService.findByLogin(principal.getName());
        if (!groupService.isOwner(id, user.getId())) {
            log.warn(String.format("Нет доступа к delete /groups/%s/users", id));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.debug(String.format("Успешное подключение к delete /groups/%s/users", id));

        if (!groupService.existsUser(id, userLogin)) {
            log.warn("Пользователь не найден");
            return new ResponseEntity<>("Пользователь не найден", HttpStatus.NOT_FOUND);
        }

        groupService.deleteUser(id, userLogin);
        log.info(String.format("Группа пользователя %s с id %s удалена", userLogin, id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{id}/users")
    public ResponseEntity<?> addUser(Principal principal, @PathVariable Long id) {
        log.debug(String.format("Успешное подключение к post /groups/%s/users", id));

        if (!groupService.existsUser(id, principal.getName())) {
            log.warn("Пользователь уже в группе");
            return new ResponseEntity<>("Пользователь уже в группе", HttpStatus.CONFLICT);
        }

        User user = userService.findByLogin(principal.getName());
        groupService.addUser(id, user.getId());
        log.info(String.format("Пользователь %s вступил в группу с id %s", principal.getName(), id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getByUser(Principal principal) {
        log.debug("Успешное подключение к get /groups/users");

        User user = userService.findByLogin(principal.getName());
        List<GroupDTO> groups = groupDTOMapper.mapListToDTO(user.getGroups());
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<?> getUsers(Principal principal, @PathVariable Long id) {
        log.debug(String.format("Успешное подключение к get /groups/%s/users", id));

        if (!groupService.existsUser(id, principal.getName())) {
            log.warn(String.format("Нет доступа к get /groups/%s/users", id));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.debug(String.format("Успешное подключение к get /groups/%s/users", id));

        List<String> users = groupService.getUsers(id);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> rename(Principal principal, @PathVariable Long id, @RequestParam String name) {
        log.debug(String.format("Успешное подключение к put /groups/%s", id));

        User user = userService.findByLogin(principal.getName());
        if (!groupService.isOwner(id, user.getId())) {
            log.warn(String.format("Нет доступа к put /groups/%s", id));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        groupService.rename(id, name);
        log.info(String.format("Группу с id %s переименована", id));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
