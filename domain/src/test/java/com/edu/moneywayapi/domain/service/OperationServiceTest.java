package com.edu.moneywayapi.domain.service;

import com.edu.moneywayapi.domain.entity.Operation;
import com.edu.moneywayapi.domain.exception.InvalidPeriodException;
import com.edu.moneywayapi.domain.repository.OperationRepository;
import com.edu.moneywayapi.domain.service.impl.OperationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationServiceTest {

    private OperationService operationService;

    @Mock
    private OperationRepository operationRepository;

    private Operation operation;

    @BeforeEach
    void setUp() {
        operation = Operation.builder().id(1L).build();
        operationService = new OperationServiceImpl(operationRepository);
    }

    @Test
    void save() {
        // setup
        when(operationRepository.save(operation)).thenReturn(operation);

        // test execution
        Operation savedOperation = operationService.save(operation);

        // test check
        assertEquals(operation, savedOperation);
        verify(operationRepository).save(operation);
    }

    @Test
    void findByCategoryAndPeriod() throws Exception {
        // setup
        LocalDateTime fromDate = LocalDateTime.of(2022, 1, 1, 12, 30);
        LocalDateTime toDate = LocalDateTime.of(2022, 1, 2, 12, 30);
        when(operationRepository.findByCategoryAndPeriod(1L, fromDate, toDate)).thenReturn(Collections.singletonList(operation));

        // test execution
        List<Operation> operations = operationService.findByCategoryAndPeriod(1L, fromDate, toDate);

        // test check
        assertEquals(Collections.singletonList(operation), operations);
        verify(operationRepository).findByCategoryAndPeriod(1L, fromDate, toDate);
    }

    @Test
    void findByCategoryAndPeriodWithInvalidData() {
        // test execution and check
        assertThatThrownBy(() -> operationService.findByCategoryAndPeriod(1L, null, null))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void findByCategoryAndPeriodWithInvalidPeriod() {
        // test execution and check
        assertThatThrownBy(() -> operationService.findByCategoryAndPeriod(1L, LocalDateTime.now(), LocalDateTime.now()))
                .isExactlyInstanceOf(InvalidPeriodException.class);
    }
}