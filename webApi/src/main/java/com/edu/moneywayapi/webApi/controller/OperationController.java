package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.service.OperationService;
import com.edu.moneywayapi.domain.service.UserService;
import com.edu.moneywayapi.webApi.context.DateOperationContext;
import com.edu.moneywayapi.webApi.dto.OperationDTO;
import com.edu.moneywayapi.webApi.dto.UserDTO;
import com.edu.moneywayapi.webApi.mapper.OperationDTOMapper;
import com.edu.moneywayapi.webApi.mapper.UserDTOMapper;
import com.edu.moneywayapi.webApi.validator.OperationValidator;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/operations")
@Slf4j
@Api(description = "Маршруты для денежных операций",
        tags = {"Operation"})
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

    @ApiOperation(value = "Добавление операции", tags = {"Operation"})
    @ApiResponses(value = {
            @ApiResponse(code = 422, message = "Невалидная операция. Возвращается список ошибок валидации."),
            @ApiResponse(code = 201, message = "Операция добавлена")})
    @PostMapping
    public ResponseEntity<?> add(@ApiParam("Добавляемая операция") @RequestBody OperationDTO operation) {
        log.debug("Успешное подключение к post /operations");

        ValidationResult validationResult = operationValidator.validate(operation);
        if (!validationResult.isValid()) {
            log.warn("Невалидная операция: " + validationResult.getErrors());
            return new ResponseEntity<>(validationResult.getErrors(), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = userDTOMapper.map(userService.findByLogin(authentication.getName()));
        operation.setUser(userDTO);
        operationService.save(operationDTOMapper.map(operation));

        log.info("Операция успешно добавлена");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "Получение операций по категории и периоду", tags = {"Operation"})
    @ApiResponses(value = {
            @ApiResponse(code = 422, message = "Невалидны данные. Возвращается информация об ошибке."),
            @ApiResponse(code = 200, message = "Операции получены. Возвращается список операций"),
            @ApiResponse(code = 404, message = "Операции не найдены")})
    @PostMapping("/filter")
    public ResponseEntity<?> getByCategoryAndPeriod(
            @RequestParam Long categoryId,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        log.debug("Успешное подключение к get /operations");

        List<OperationDTO> operations;
        try {
            operations = operationDTOMapper.mapListToDTO(operationService.findByCategoryAndPeriod(
                    categoryId, LocalDateTime.parse(fromDate), LocalDateTime.parse(toDate)));
        } catch (Exception e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return operations != null && !operations.isEmpty()
                ? new ResponseEntity<>(operations, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
