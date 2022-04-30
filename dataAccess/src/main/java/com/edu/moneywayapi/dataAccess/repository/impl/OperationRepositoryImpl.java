package com.edu.moneywayapi.dataAccess.repository.impl;

import com.edu.moneywayapi.dataAccess.mapper.CategoryDALMapper;
import com.edu.moneywayapi.dataAccess.mapper.OperationDALMapper;
import com.edu.moneywayapi.dataAccess.repository.jpa.JpaOperationRepository;
import com.edu.moneywayapi.domain.entity.Operation;
import com.edu.moneywayapi.domain.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OperationRepositoryImpl implements OperationRepository {

    private final JpaOperationRepository jpaOperationRepository;
    private final OperationDALMapper operationDALMapper;

    @Autowired
    public OperationRepositoryImpl(JpaOperationRepository jpaOperationRepository, OperationDALMapper operationDALMapper) {
        this.jpaOperationRepository = jpaOperationRepository;
        this.operationDALMapper = operationDALMapper;
    }

    @Override
    public Operation save(Operation operation) {
        return operationDALMapper.map(jpaOperationRepository.save(operationDALMapper.map(operation)));
    }

    @Override
    public List<Operation> findByCategoryAndPeriod(Long categoryId, LocalDateTime fromDate, LocalDateTime toDate) {
        return operationDALMapper.mapListToEntity(jpaOperationRepository.findByCategoryAndPeriod(
                categoryId, fromDate, toDate));
    }
}
