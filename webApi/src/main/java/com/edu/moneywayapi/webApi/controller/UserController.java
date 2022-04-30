package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.service.UserService;
import com.edu.moneywayapi.webApi.dto.UserDTO;
import com.edu.moneywayapi.webApi.mapper.UserDTOMapper;
import com.edu.moneywayapi.webApi.validator.UserValidator;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.regex.Pattern;

@RestController
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final UserDTOMapper userDTOMapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Value("${user.email.not-match}")
    private String isEmailNotMatchFormatMessage;
    @Value("${format.email}")
    private String formatEmail;

    @Value("${user.login.size.min}")
    private Integer minSizeLogin;
    @Value("${user.login.size.max}")
    private Integer maxSizeLogin;
    @Value("${user.login.size.message}")
    private String isIncorrectSizeLoginMessage;

    @Value("${user.password.size.min}")
    private Integer minSizePassword;
    @Value("${user.password.size.max}")
    private Integer maxSizePassword;
    @Value("${user.password.size.message}")
    private String isIncorrectSizePasswordMessage;

    @Autowired
    public UserController(UserService userService, UserValidator userValidator, UserDTOMapper userDTOMapper,
                          AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.userDTOMapper = userDTOMapper;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO user) {
        log.debug("Успешное подключение к post /login");

        User userFromDb = userService.findByEmail(user.getEmail());

        if (userFromDb == null) {
            log.warn("Пользователь не найден");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!passwordEncoder.matches(user.getPassword(), userFromDb.getPassword())) {
            log.warn("Неверный пароль");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userFromDb.getLogin(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Авторизация прошла успешно");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO user) {
        log.debug("Успешное подключение к post /register");

        ValidationResult validationResult = userValidator.validate(user);
        if (!validationResult.isValid()) {
            log.warn("Невалидный пользователь: " + validationResult.getErrors());
            return new ResponseEntity<>(validationResult.getErrors(), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (userService.existsByEmail(user.getEmail())) {
            log.warn("Email уже используется");
            return new ResponseEntity<>("Email уже используется", HttpStatus.CONFLICT);
        }

        if (userService.existsByLogin(user.getLogin())) {
            log.warn("Логин уже используется");
            return new ResponseEntity<>("Логин уже используется", HttpStatus.CONFLICT);
        }

        userService.save(userDTOMapper.map(
                UserDTO.builder()
                        .email(user.getEmail())
                        .login(user.getLogin())
                        .password(passwordEncoder.encode(user.getPassword()))
                        .groups(new ArrayList<>())
                        .categories(new ArrayList<>())
                        .build()));

        log.info("Регистрация прошла успешно");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/users/profile")
    public ResponseEntity<?> get(Principal principal) {
        log.debug("Успешное подключение к get /users/profile");

        UserDTO user = userDTOMapper.map(userService.findByLogin(principal.getName()));

        return new ResponseEntity<>(
                UserDTO.builder()
                        .email(user.getEmail())
                        .login(user.getLogin()).build(),
                HttpStatus.OK);
    }

    @PutMapping("/users/profile/email")
    public ResponseEntity<?> updateEmail(Principal principal, @RequestBody String requestJson) {
        log.debug("Успешное подключение к put /users/profile/email");

        String email = JsonPath.read(requestJson, "$.email");

        if (email == null) {
            log.warn("Email отсутствует");
            return new ResponseEntity<>("Email отсутствует", HttpStatus.BAD_REQUEST);
        }

        if (!Pattern.matches(formatEmail, email)) {
            log.warn(isEmailNotMatchFormatMessage);
            return new ResponseEntity<>(isEmailNotMatchFormatMessage, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (userService.existsByEmail(email)) {
            log.warn("Email уже используется");
            return new ResponseEntity<>("Email уже используется", HttpStatus.CONFLICT);
        }

        UserDTO userFromDb = userDTOMapper.map(userService.findByLogin(principal.getName()));
        userService.updateEmail(email, userFromDb.getId());
        log.info("Email изменён");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/users/profile/login")
    public ResponseEntity<?> updateLogin(Principal principal, @RequestBody String responseJson) {
        log.debug("Успешное подключение к put /users/profile/login");

        String login = JsonPath.read(responseJson, "$.login");

        if (login == null) {
            log.warn("Логин отсутствует");
            return new ResponseEntity<>("Логин отсутствует", HttpStatus.BAD_REQUEST);
        }

        if (minSizeLogin > login.length() || login.length() > maxSizeLogin) {
            log.warn(isIncorrectSizeLoginMessage);
            return new ResponseEntity<>(isIncorrectSizeLoginMessage, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (userService.existsByLogin(login)) {
            log.warn("Логин уже используется");
            return new ResponseEntity<>("Логин уже используется", HttpStatus.CONFLICT);
        }

        UserDTO userFromDb = userDTOMapper.map(userService.findByLogin(principal.getName()));
        userService.updateLogin(login, userFromDb.getId());
        log.info("Логин изменён");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/users/profile/password")
    public ResponseEntity<?> updatePassword(Principal principal, @RequestBody String responseJson) {
        log.debug("Успешное подключение к put /users/profile/password");

        String password = JsonPath.read(responseJson, "$.password");

        if (password == null) {
            log.warn("Пароль отсутствует");
            return new ResponseEntity<>("Пароль отсутствует", HttpStatus.BAD_REQUEST);
        }

        if (minSizePassword > password.length() || password.length() > maxSizePassword) {
            log.warn(isIncorrectSizePasswordMessage);
            return new ResponseEntity<>(isIncorrectSizePasswordMessage, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        UserDTO userFromDb = userDTOMapper.map(userService.findByLogin(principal.getName()));
        userService.updatePassword(passwordEncoder.encode(password), userFromDb.getId());
        log.info("Пароль изменён");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
