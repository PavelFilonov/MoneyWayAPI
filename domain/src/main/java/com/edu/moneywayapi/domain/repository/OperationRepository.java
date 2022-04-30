package com.edu.moneywayapi.domain.repository;

import com.edu.moneywayapi.domain.entity.Operation;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationRepository {

    Operation save(Operation operation);

    List<Operation> findByCategoryAndPeriod(Long categoryId, LocalDateTime fromDate, LocalDateTime toDate);
}
