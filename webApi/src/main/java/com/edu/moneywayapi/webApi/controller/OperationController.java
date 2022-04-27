package com.edu.moneywayapi.webApi.controller;

import br.com.fluentvalidator.context.ValidationResult;
import com.edu.moneywayapi.domain.entity.TypeOperation;
import com.edu.moneywayapi.domain.service.OperationService;
import com.edu.moneywayapi.webApi.dto.CategoryDTO;
import com.edu.moneywayapi.webApi.dto.OperationDTO;
import com.edu.moneywayapi.webApi.mapper.CategoryDTOMapper;
import com.edu.moneywayapi.webApi.mapper.OperationDTOMapper;
import com.edu.moneywayapi.webApi.validator.OperationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/operation")
public class OperationController {

    @Autowired
    private OperationService operationService;

    @Autowired
    private OperationValidator operationValidator;

    @Autowired
    private OperationDTOMapper operationDALMapper;
    @Autowired
    private CategoryDTOMapper categoryDTOMapper;

    @GetMapping
    public ResponseEntity<?> getByCategory(@RequestBody CategoryDTO category) {
        List<OperationDTO> operations = operationDALMapper.mapListToDTO(
                operationService.findByCategory(categoryDTOMapper.map(category)));

        return operations != null && !operations.isEmpty()
                ? new ResponseEntity<>(operations, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody OperationDTO operation) {
        ValidationResult validationResult = operationValidator.validate(operation);
        if (!validationResult.isValid())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        operationService.save(operationDALMapper.map(operation));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("{userId}")
    public ResponseEntity<?> getByUserIdAndTypeOperationAndPeriod(@PathVariable(name = "userId") Long userId,
                                                                  @RequestBody TypeOperation typeOperation,
                                                                  @RequestBody LocalDateTime fromDate,
                                                                  @RequestBody LocalDateTime toDate) {
        try {
            List<OperationDTO> operations = operationDALMapper.mapListToDTO(
                    operationService.findByUserIdAndTypeOperationAndPeriod(userId, typeOperation, fromDate, toDate));
            return new ResponseEntity<>(operations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
