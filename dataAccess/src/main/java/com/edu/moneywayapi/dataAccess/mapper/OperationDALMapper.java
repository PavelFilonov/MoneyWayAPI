package com.edu.moneywayapi.dataAccess.mapper;

import com.edu.moneywayapi.dataAccess.dal.OperationDAL;
import com.edu.moneywayapi.domain.entity.Operation;
import com.edu.moneywayapi.domain.mapper.OperationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OperationDALMapper implements OperationMapper<OperationDAL> {

    private final CategoryDALMapper categoryDALMapper;
    private final UserDALMapper userDALMapper;

    @Autowired
    public OperationDALMapper(CategoryDALMapper categoryDALMapper, UserDALMapper userDALMapper) {
        this.categoryDALMapper = categoryDALMapper;
        this.userDALMapper = userDALMapper;
    }

    @Override
    public OperationDAL map(Operation operation) {
        if (operation == null)
            return null;

        return OperationDAL.builder()
                .id(operation.getId())
                .type(operation.getType())
                .categoryDAL(categoryDALMapper.map(operation.getCategory()))
                .userDAL(userDALMapper.map(operation.getUser()))
                .value(operation.getValue())
                .createdAt(operation.getCreatedAt())
                .build();
    }

    @Override
    public Operation map(OperationDAL obj) {
        if (obj == null)
            return null;

        return Operation.builder()
                .id(obj.getId())
                .type(obj.getType())
                .category(categoryDALMapper.map(obj.getCategoryDAL()))
                .user(userDALMapper.map(obj.getUserDAL()))
                .value(obj.getValue())
                .createdAt(obj.getCreatedAt())
                .build();
    }

    public List<Operation> mapListToEntity(List<OperationDAL> operationsDAL) {
        List<Operation> operations = new ArrayList<>();

        for (OperationDAL operationDAL : operationsDAL) {
            operations.add(map(operationDAL));
        }

        return operations;
    }
}
