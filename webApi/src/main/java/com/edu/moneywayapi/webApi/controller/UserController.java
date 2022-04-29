package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.entity.User;
import com.edu.moneywayapi.domain.service.UserService;
import com.edu.moneywayapi.webApi.dto.UserDTO;
import com.edu.moneywayapi.webApi.mapper.UserDTOMapper;
import com.edu.moneywayapi.webApi.validator.UserValidator;
import com.jayway.jsonpath.JsonPath;
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
        User userFromDb = userService.findByEmail(user.getEmail());

        if (userFromDb == null)
            return new ResponseEntity<>("Пользователь не найден", HttpStatus.NOT_FOUND);

        if (!passwordEncoder.matches(user.getPassword(), userFromDb.getPassword()))
            return new ResponseEntity<>("Неверный пароль", HttpStatus.CONFLICT);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userFromDb.getLogin(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO user) {
        ValidationResult validationResult = userValidator.validate(user);
        if (!validationResult.isValid())
            return new ResponseEntity<>(validationResult.getErrors(), HttpStatus.UNPROCESSABLE_ENTITY);

        if (userService.existsByEmail(user.getEmail()))
            return new ResponseEntity<>("Email уже используется", HttpStatus.CONFLICT);

        if (userService.existsByLogin(user.getLogin()))
            return new ResponseEntity<>("Логин уже используется", HttpStatus.CONFLICT);

        userService.save(userDTOMapper.map(
                UserDTO.builder()
                        .email(user.getEmail())
                        .login(user.getLogin())
                        .password(passwordEncoder.encode(user.getPassword()))
                        .groups(new ArrayList<>())
                        .categories(new ArrayList<>())
                        .build()));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @GetMapping("/users/profile")
    public ResponseEntity<?> get(Principal principal) {
        UserDTO user = userDTOMapper.map(userService.findByLogin(principal.getName()));

        return new ResponseEntity<>(
                UserDTO.builder()
                        .email(user.getEmail())
                        .login(user.getLogin()).build(),
                HttpStatus.OK);
    }

    @PutMapping("/users/profile/email")
    public ResponseEntity<?> updateEmail(Principal principal, @RequestBody String responseJson) {
        String email = JsonPath.read(responseJson, "$.email");

        if (!Pattern.matches(formatEmail, email))
            return new ResponseEntity<>(isEmailNotMatchFormatMessage, HttpStatus.UNPROCESSABLE_ENTITY);

        if (userService.existsByEmail(email))
            return new ResponseEntity<>("Email уже используется", HttpStatus.CONFLICT);

        UserDTO userFromDb = userDTOMapper.map(userService.findByLogin(principal.getName()));
        userService.updateEmail(email, userFromDb.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/users/profile/login")
    public ResponseEntity<?> updateLogin(Principal principal, @RequestBody String responseJson) {
        String login = JsonPath.read(responseJson, "$.login");

        if (minSizeLogin > login.length() || login.length() > maxSizeLogin)
            return new ResponseEntity<>(isIncorrectSizeLoginMessage, HttpStatus.UNPROCESSABLE_ENTITY);

        if (userService.existsByLogin(login))
            return new ResponseEntity<>("Логин уже используется", HttpStatus.CONFLICT);

        UserDTO userFromDb = userDTOMapper.map(userService.findByLogin(principal.getName()));
        userService.updateLogin(login, userFromDb.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/users/profile/password")
    public ResponseEntity<?> updatePassword(Principal principal, @RequestBody String responseJson) {
        String password = JsonPath.read(responseJson, "$.password");

        if (minSizePassword > password.length() || password.length() > maxSizePassword)
            return new ResponseEntity<>(isIncorrectSizePasswordMessage, HttpStatus.UNPROCESSABLE_ENTITY);

        UserDTO userFromDb = userDTOMapper.map(userService.findByLogin(principal.getName()));
        userService.updatePassword(passwordEncoder.encode(password), userFromDb.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
