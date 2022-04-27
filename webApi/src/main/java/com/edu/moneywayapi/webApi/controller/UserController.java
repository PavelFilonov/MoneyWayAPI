package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.exception.NoSuchUserException;
import com.edu.moneywayapi.domain.service.UserService;
import com.edu.moneywayapi.webApi.dto.UserDTO;
import com.edu.moneywayapi.webApi.mapper.UserDTOMapper;
import com.edu.moneywayapi.webApi.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UserDTOMapper userDTOMapper;

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") Long id) {
        try {
            UserDTO user = userDTOMapper.map(userService.findById(id));
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (NoSuchUserException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> login(@PathVariable(name = "email") String email) {
        UserDTO user = userDTOMapper.map(userService.findByEmail(email));

        return user != null
                ? new ResponseEntity<>(user, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody UserDTO user) {
        ValidationResult validationResult = userValidator.validate(user);
        if (!validationResult.isValid())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        userService.save(userDTOMapper.map(user));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id, @RequestBody UserDTO user) {
        try {
            userDTOMapper.map(userService.findById(id));
        } catch (NoSuchUserException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ValidationResult validationResult = userValidator.validate(user);
        if (!validationResult.isValid())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        userService.update(userDTOMapper.map(user), id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
