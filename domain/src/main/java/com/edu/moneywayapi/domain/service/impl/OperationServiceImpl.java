package com.edu.moneywayapi.domain.service.impl;

import com.edu.moneywayapi.domain.entity.Operation;
import com.edu.moneywayapi.domain.exception.InvalidPeriodException;
import com.edu.moneywayapi.domain.repository.OperationRepository;
import com.edu.moneywayapi.domain.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;

    @Autowired
    public OperationServiceImpl(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    @Override
    public Operation save(Operation operation) {
        return operationRepository.save(operation);
    }

    @Override
    public List<Operation> findByCategoryAndPeriod(Long categoryId, LocalDateTime fromDate, LocalDateTime toDate) throws Exception {
        if (categoryId == null || fromDate == null || toDate == null)
            throw new NullPointerException("Неверные данные");

        if (fromDate.isEqual(toDate) || fromDate.isAfter(toDate))
            throw new InvalidPeriodException("Дата окончания раньше даты начала либо равно ему");

        return operationRepository.findByCategoryAndPeriod(categoryId, fromDate, toDate);
    }
}
