package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.service.UserService;
import com.edu.moneywayapi.webApi.config.jwt.JwtTokenProvider;
import com.edu.moneywayapi.webApi.dto.UserDTO;
import com.edu.moneywayapi.webApi.mapper.UserDTOMapper;
import com.edu.moneywayapi.webApi.response.AuthResponse;
import com.edu.moneywayapi.webApi.validator.UserValidator;
import io.swagger.annotations.*;
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

import java.util.ArrayList;
import java.util.regex.Pattern;

@RestController
@Slf4j
@Api(description = "Маршруты для пользователя",
        tags = {"User"})
public class UserController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final UserDTOMapper userDTOMapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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
                          AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.userDTOMapper = userDTOMapper;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @ApiOperation(value = "Авторизация пользователя", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 409, message = "Неверный пароль"),
            @ApiResponse(code = 200, message = "Авторизация прошла успешно")})
    @PostMapping("/login")
    public ResponseEntity<?> login(@ApiParam("Авторизируемый пользователь") @RequestBody UserDTO user) {
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

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userFromDb.getLogin(), user.getPassword()));
        String token = jwtTokenProvider.createToken(userFromDb.getLogin());
        AuthResponse authResponse = new AuthResponse(userDTOMapper.map(userFromDb), token);

        log.info("Авторизация прошла успешно");
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Регистрация пользователя", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(code = 422, message = "Невалидный пользователь. Возращается список ошибок валидации."),
            @ApiResponse(code = 409, message = "Данные уже используются. Возвращается информация об ошибке."),
            @ApiResponse(code = 201, message = "Регистрация прошла успешно")})
    @PostMapping("/register")
    public ResponseEntity<?> register(@ApiParam("Регистрируемый пользователь") @RequestBody UserDTO user) {
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

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword()));
        String token = jwtTokenProvider.createToken(user.getLogin());
        AuthResponse authResponse = new AuthResponse(user, token);

        log.info("Регистрация прошла успешно");
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Получение авторизованного пользователя", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пользователь получен")})
    @GetMapping("/users/profile")
    public ResponseEntity<?> get() {
        log.debug("Успешное подключение к get /users/profile");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO user = userDTOMapper.map(userService.findByLogin(authentication.getName()));

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @ApiOperation(value = "Получение имени пользователя по id", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ответ получен")})
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getLoginById(@PathVariable Long id) {
        log.debug("Успешное подключение к get /users/profile");

        User user = userService.findById(id);
        return new ResponseEntity<>(UserDTO.builder().login(user.getLogin()), HttpStatus.OK);
    }

    @ApiOperation(value = "Обновление email пользователя", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(code = 422, message = "Невалидный email. Возращается информация об ошибке."),
            @ApiResponse(code = 409, message = "Email уже используется. Возвращается информация об ошибке."),
            @ApiResponse(code = 200, message = "Email изменён")})
    @PutMapping("/users/profile/email")
    public ResponseEntity<?> updateEmail(@ApiParam("Новый email") @RequestParam String email) {
        log.debug("Успешное подключение к put /users/profile/email");

        if (!Pattern.matches(formatEmail, email)) {
            log.warn(isEmailNotMatchFormatMessage);
            return new ResponseEntity<>(isEmailNotMatchFormatMessage, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (userService.existsByEmail(email)) {
            log.warn("Email уже используется");
            return new ResponseEntity<>("Email уже используется", HttpStatus.CONFLICT);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userFromDb = userDTOMapper.map(userService.findByLogin(authentication.getName()));
        userService.updateEmail(email, userFromDb.getId());
        log.info("Email изменён");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Обновление логина пользователя", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(code = 422, message = "Невалидный логин. Возращается информация об ошибке."),
            @ApiResponse(code = 409, message = "Логин уже используется. Возвращается информация об ошибке."),
            @ApiResponse(code = 200, message = "Логин изменён")})
    @PutMapping("/users/profile/login")
    public ResponseEntity<?> updateLogin(@ApiParam("Новый логин") @RequestParam String login) {
        log.debug("Успешное подключение к put /users/profile/login");

        if (minSizeLogin > login.length() || login.length() > maxSizeLogin) {
            log.warn(isIncorrectSizeLoginMessage);
            return new ResponseEntity<>(isIncorrectSizeLoginMessage, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (userService.existsByLogin(login)) {
            log.warn("Логин уже используется");
            return new ResponseEntity<>("Логин уже используется", HttpStatus.CONFLICT);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userFromDb = userDTOMapper.map(userService.findByLogin(authentication.getName()));
        userService.updateLogin(login, userFromDb.getId());
        log.info("Логин изменён");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Обновление пароля пользователя", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(code = 422, message = "Невалидный пароль. Возращается информация об ошибке."),
            @ApiResponse(code = 200, message = "Пароль изменён")})
    @PutMapping("/users/profile/password")
    public ResponseEntity<?> updatePassword(@ApiParam("Новый пароль") @RequestParam String password) {
        log.debug("Успешное подключение к put /users/profile/password");

        if (minSizePassword > password.length() || password.length() > maxSizePassword) {
            log.warn(isIncorrectSizePasswordMessage);
            return new ResponseEntity<>(isIncorrectSizePasswordMessage, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userFromDb = userDTOMapper.map(userService.findByLogin(authentication.getName()));
        userService.updatePassword(passwordEncoder.encode(password), userFromDb.getId());
        log.info("Пароль изменён");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
