package com.edu.moneywayapi.domain.service;

import com.edu.moneywayapi.domain.entity.Operation;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationService {

    Operation save(Operation operation);

    List<Operation> findByCategoryAndPeriod(Long categoryId, LocalDateTime fromDate, LocalDateTime toDate) throws Exception;
}
