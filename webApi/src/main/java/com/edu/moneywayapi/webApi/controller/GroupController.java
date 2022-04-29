package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.exception.NoSuchGroupException;
import com.edu.moneywayapi.domain.exception.NoSuchUserException;
import com.edu.moneywayapi.domain.service.GroupService;
import com.edu.moneywayapi.webApi.dto.GroupDTO;
import com.edu.moneywayapi.webApi.mapper.GroupDTOMapper;
import com.edu.moneywayapi.webApi.validator.GroupValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private GroupDTOMapper groupDTOMapper;

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(name = "id") Long id) {
        try {
            GroupDTO group = groupDTOMapper.map(groupService.findById(id));
            return new ResponseEntity<>(group, HttpStatus.OK);
        } catch (NoSuchGroupException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> getByToken(@PathVariable(name = "token") String token) {
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

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable(name = "id") Long id) {
        try {
            groupService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchGroupException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}/user")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestParam String userLogin) {
        groupService.deleteUser(id, userLogin);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
