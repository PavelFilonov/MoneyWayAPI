package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.exception.NoSuchGroupException;
import com.edu.moneywayapi.domain.service.GroupService;
import com.edu.moneywayapi.domain.service.UserService;
import com.edu.moneywayapi.webApi.dto.GroupDTO;
import com.edu.moneywayapi.webApi.mapper.GroupDTOMapper;
import com.edu.moneywayapi.webApi.validator.GroupValidator;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/groups")
@Slf4j
@Api(description = "Маршруты для групп",
        tags = {"Group"})
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

    @ApiOperation(value = "Получение группы по id", tags = {"Group"})
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Нет доступа к группе"),
            @ApiResponse(code = 200, message = "Группа получена. Возвращается группа."),
            @ApiResponse(code = 400, message = "Группа не найдена")})
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@ApiParam("Id группы") @PathVariable Long id) {
        log.debug(String.format("Успешное подключение к get /groups/%s", id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!groupService.existsUser(id, authentication.getName()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try {
            GroupDTO group = groupDTOMapper.map(groupService.findById(id));
            return new ResponseEntity<>(group, HttpStatus.OK);
        } catch (NoSuchGroupException e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Получение группы по токену", tags = {"Group"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Группа получена. Возвращается группа."),
            @ApiResponse(code = 400, message = "Группа не найдена")})
    @GetMapping()
    public ResponseEntity<?> getByToken(@ApiParam("Токен группы") @RequestParam String token) {
        log.debug("Успешное подключение к get /groups");

        try {
            GroupDTO group = groupDTOMapper.map(groupService.findByToken(token));
            return new ResponseEntity<>(group, HttpStatus.OK);
        } catch (NoSuchGroupException e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Добавление группы", tags = {"Group"})
    @ApiResponses(value = {
            @ApiResponse(code = 422, message = "Невалидная группа. Возвращается список ошибок валидации."),
            @ApiResponse(code = 201, message = "Группа добавлена")})
    @PostMapping
    public ResponseEntity<?> add(@ApiParam("Добавляемая группа") @RequestBody GroupDTO group) {
        log.debug("Успешное подключение к post /groups");

        ValidationResult validationResult = groupValidator.validate(group);
        if (!validationResult.isValid()) {
            log.warn("Невалидная группа: " + validationResult.getErrors());
            return new ResponseEntity<>(validationResult.getErrors(), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByLogin(authentication.getName());

        GroupDTO groupDTO = groupDTOMapper.map(groupService.save(groupDTOMapper.map(group), user.getId()));
        log.info("Группа добавлена");
        return new ResponseEntity<>(groupDTO, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Удаление группы по id", tags = {"Group"})
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Нет доступа к удалению группы"),
            @ApiResponse(code = 200, message = "Группа удалена"),
            @ApiResponse(code = 400, message = "Группа не найдена")})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@ApiParam("Id группы") @PathVariable(name = "id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByLogin(authentication.getName());
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

    @ApiOperation(value = "Удаление пользователя группы", tags = {"Group"})
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Нет доступа к удалению пользователя группы"),
            @ApiResponse(code = 400, message = "Пользователь не найден"),
            @ApiResponse(code = 200, message = "Пользователь удалён")})
    @DeleteMapping("/{id}/users")
    public ResponseEntity<?> deleteUser(@ApiParam("Id группы") @PathVariable Long id,
                                        @ApiParam("Логин удаляемого пользователя") @RequestParam String userLogin) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByLogin(authentication.getName());
        if (!groupService.isOwner(id, user.getId())) {
            log.warn(String.format("Нет доступа к delete /groups/%s/users", id));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.debug(String.format("Успешное подключение к delete /groups/%s/users", id));

        if (!groupService.existsUser(id, userLogin)) {
            log.warn("Пользователь не найден");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        groupService.deleteUser(id, userLogin);
        log.info(String.format("Пользователь %s группы с id %s удалён", userLogin, id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Добавления пользователя в группу", tags = {"Group"})
    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "Пользователь уже в группе"),
            @ApiResponse(code = 201, message = "Пользователь вступил в группу")})
    @PostMapping("/{id}/users")
    public ResponseEntity<?> addUser(@ApiParam("Id группы") @PathVariable Long id) {
        log.debug(String.format("Успешное подключение к post /groups/%s/users", id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (groupService.existsUser(id, authentication.getName())) {
            log.warn("Пользователь уже в группе");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        User user = userService.findByLogin(authentication.getName());
        groupService.addUser(id, user.getId());
        log.info(String.format("Пользователь %s вступил в группу с id %s", authentication.getName(), id));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "Получение групп пользователя", tags = {"Group"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Группы пользователя получены. Возвращается список групп.")})
    @GetMapping("/users")
    public ResponseEntity<?> getByUser() {
        log.debug("Успешное подключение к get /groups/users");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByLogin(authentication.getName());

        List<GroupDTO> groups = groupDTOMapper.mapListToDTO(groupService.findByUser(user.getId()));

        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @ApiOperation(value = "Получение пользователей группы", tags = {"Group"})
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Нет доступа к данным"),
            @ApiResponse(code = 200, message = "Пользователи группы получены. Возвращается список логинов пользователей")})
    @GetMapping("/{id}/users")
    public ResponseEntity<?> getUsers(@ApiParam("Id группы") @PathVariable Long id) {
        log.debug(String.format("Успешное подключение к get /groups/%s/users", id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!groupService.existsUser(id, authentication.getName())) {
            log.warn(String.format("Нет доступа к get /groups/%s/users", id));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.debug(String.format("Успешное подключение к get /groups/%s/users", id));

        List<String> users = groupService.getUsers(id);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @ApiOperation(value = "Переименование группы", tags = {"Group"})
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Нет доступа к переименованию группы"),
            @ApiResponse(code = 200, message = "Группа переименована")})
    @PutMapping("/{id}")
    public ResponseEntity<?> rename(@ApiParam("Id группы") @PathVariable Long id,
                                    @ApiParam("Новое название группы") @RequestParam String name) {
        log.debug(String.format("Успешное подключение к put /groups/%s", id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByLogin(authentication.getName());
        if (!groupService.isOwner(id, user.getId())) {
            log.warn(String.format("Нет доступа к put /groups/%s", id));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        groupService.rename(id, name);
        log.info(String.format("Группу с id %s переименована", id));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
