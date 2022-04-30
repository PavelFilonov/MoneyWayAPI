package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.service.OperationService;
import com.edu.moneywayapi.domain.service.UserService;
import com.edu.moneywayapi.webApi.context.OperationRequestContext;
import com.edu.moneywayapi.webApi.dto.OperationDTO;
import com.edu.moneywayapi.webApi.dto.UserDTO;
import com.edu.moneywayapi.webApi.mapper.OperationDTOMapper;
import com.edu.moneywayapi.webApi.mapper.UserDTOMapper;
import com.edu.moneywayapi.webApi.validator.OperationValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/operations")
@Slf4j
public class OperationController {

    private final OperationService operationService;
    private final OperationValidator operationValidator;
    private final OperationDTOMapper operationDTOMapper;
    private final UserService userService;
    private final UserDTOMapper userDTOMapper;

    @Autowired
    public OperationController(OperationService operationService, OperationValidator operationValidator,
                               OperationDTOMapper operationDTOMapper, UserService userService, UserDTOMapper userDTOMapper) {
        this.operationService = operationService;
        this.operationValidator = operationValidator;
        this.operationDTOMapper = operationDTOMapper;
        this.userService = userService;
        this.userDTOMapper = userDTOMapper;
    }

    @PostMapping
    public ResponseEntity<?> add(Principal principal, @RequestBody OperationDTO operation) {
        log.debug("Успешное подключение к post /operations");

        ValidationResult validationResult = operationValidator.validate(operation);
        if (!validationResult.isValid()) {
            log.warn("Невалидная операция: " + validationResult.getErrors());
            return new ResponseEntity<>(validationResult.getErrors(), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        UserDTO userDTO = userDTOMapper.map(userService.findByLogin(principal.getName()));
        operation.setUserDTO(userDTO);
        operationService.save(operationDTOMapper.map(operation));
        log.info("Операция успешно добавлена");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getByCategoryAndPeriod(@RequestBody OperationRequestContext operationRequestContext) {
        log.debug("Успешное подключение к get /operations");

        List<OperationDTO> operations;
        try {
            operations = operationDTOMapper.mapListToDTO(operationService.findByCategoryAndPeriod(
                    operationRequestContext.getCategoryId(),
                    operationRequestContext.getFromDate(), operationRequestContext.getToDate()));
        } catch (Exception e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return operations != null && !operations.isEmpty()
                ? new ResponseEntity<>(operations, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
