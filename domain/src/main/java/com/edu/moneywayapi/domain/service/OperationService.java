package com.edu.moneywayapi.domain.service;

import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.entity.Operation;
import com.edu.moneywayapi.domain.entity.TypeOperation;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationService {
    List<Operation> findByCategory(Category category);

    Operation save(Operation operation);

    List<Operation> findByUserIdAndTypeOperationAndPeriod(Long userId, TypeOperation typeOperation,
                                                          LocalDateTime fromDate, LocalDateTime toDate) throws Exception;
}
