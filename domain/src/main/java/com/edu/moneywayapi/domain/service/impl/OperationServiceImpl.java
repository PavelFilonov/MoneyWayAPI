package com.edu.moneywayapi.domain.service.impl;

import com.edu.moneywayapi.domain.entity.Category;
import com.edu.moneywayapi.domain.entity.Operation;
import com.edu.moneywayapi.domain.entity.TypeOperation;
import com.edu.moneywayapi.domain.exception.InvalidPeriodException;
import com.edu.moneywayapi.domain.exception.NoSuchUserException;
import com.edu.moneywayapi.domain.repository.OperationRepository;
import com.edu.moneywayapi.domain.service.OperationService;
import com.edu.moneywayapi.domain.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class OperationServiceImpl implements OperationService {

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private UserService userService;

    @Override
    public List<Operation> findByCategory(Category category) {
        return operationRepository.findByCategory(category);
    }

    @Override
    public Operation save(Operation operation) {
        return operationRepository.save(operation);
    }

    @Override
    public List<Operation> findByUserIdAndTypeOperationAndPeriod(Long userId, TypeOperation typeOperation,
                                                                 LocalDateTime fromDate, LocalDateTime toDate) throws Exception {

        if (userId == null || typeOperation == null || fromDate == null || toDate == null)
            throw new NullPointerException("Invalid object was found");

        if (fromDate.isEqual(toDate) || fromDate.isAfter(toDate))
            throw new InvalidPeriodException("Cannot have from date earlier or equal than now");

        if (!userService.existsById(userId))
            throw new NoSuchUserException(String.format("The user with id %s was not found", userId));

        return operationRepository.findByUserIdAndTypeOperationAndPeriod(
                userId, typeOperation.name(), fromDate, toDate);
    }
}
