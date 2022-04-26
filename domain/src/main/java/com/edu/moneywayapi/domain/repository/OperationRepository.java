package com.edu.moneywayapi.domain.repository;

import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.entity.Operation;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationRepository {
    List<Operation> findByCategory(Category category);

    Operation save(Operation operation);

    List<Operation> findByUserIdAndTypeOperationAndPeriod(Long userId, String typeOperation,
                                                          LocalDateTime fromDate, LocalDateTime toDate);
}
