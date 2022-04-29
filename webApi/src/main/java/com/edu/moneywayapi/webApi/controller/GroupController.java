package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.entity.Group;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.exception.NoSuchGroupException;
import com.edu.moneywayapi.domain.service.GroupService;
import com.edu.moneywayapi.domain.service.UserService;
import com.edu.moneywayapi.webApi.dto.GroupDTO;
import com.edu.moneywayapi.webApi.mapper.GroupDTOMapper;
import com.edu.moneywayapi.webApi.validator.GroupValidator;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private GroupDTOMapper groupDTOMapper;

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(Principal principal, @PathVariable(name = "id") Long id) {
        if (!groupService.existsUser(id, principal.getName()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try {
            GroupDTO group = groupDTOMapper.map(groupService.findById(id));
            return new ResponseEntity<>(group, HttpStatus.OK);
        } catch (NoSuchGroupException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getByToken(@RequestBody String requestJson) {
        String token = JsonPath.read(requestJson, "$.token");
        if (token == null)
            return new ResponseEntity<>("Токен отсутствует", HttpStatus.BAD_REQUEST);

        try {
            GroupDTO group = groupDTOMapper.map(groupService.findByToken(token));
            return new ResponseEntity<>(group, HttpStatus.OK);
        } catch (NoSuchGroupException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody GroupDTO group) {
        ValidationResult validationResult = groupValidator.validate(group);
        if (!validationResult.isValid())
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);

        groupService.save(groupDTOMapper.map(group));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(Principal principal, @PathVariable(name = "id") Long id) {
        User user = userService.findByLogin(principal.getName());
        if (!groupService.isOwner(id, user.getId()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try {
            groupService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchGroupException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}/user")
    public ResponseEntity<?> deleteUser(Principal principal, @PathVariable Long id, @RequestBody String requestJson) {
        User user = userService.findByLogin(principal.getName());
        if (!groupService.isOwner(id, user.getId()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        String userLogin = JsonPath.read(requestJson, "$.userLogin");
        if (userLogin == null)
            return new ResponseEntity<>("Логин пользователя отсутствует", HttpStatus.BAD_REQUEST);

        if (!groupService.existsUser(id, userLogin))
            return new ResponseEntity<>("Пользователь не найден", HttpStatus.BAD_REQUEST);

        groupService.deleteUser(id, userLogin);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{id}/user")
    public ResponseEntity<?> addUser(Principal principal, @PathVariable Long id) {
        if (!groupService.existsUser(id, principal.getName()))
            return new ResponseEntity<>("Пользователь уже в группе", HttpStatus.CONFLICT);

        User user = userService.findByLogin(principal.getName());
        groupService.addUser(id, user.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getByUser(Principal principal) {
        User user = userService.findByLogin(principal.getName());
        List<Group> groups = user.getGroups();
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<?> getUsers(Principal principal, @PathVariable Long id) {
        if (!groupService.existsUser(id, principal.getName()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        List<String> users = groupService.getUsers(id);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> rename(Principal principal, @PathVariable Long id, @RequestBody String requestJson) {
        User user = userService.findByLogin(principal.getName());
        if (!groupService.isOwner(id, user.getId()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        String name = JsonPath.read(requestJson, "$.name");
        if (name == null)
            return new ResponseEntity<>("Новое имя группы отсутствует", HttpStatus.BAD_REQUEST);

        groupService.rename(id, name);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
