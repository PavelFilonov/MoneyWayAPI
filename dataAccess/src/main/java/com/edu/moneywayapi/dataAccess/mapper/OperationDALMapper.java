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

    @Autowired
    private CategoryDALMapper categoryDALMapper;

    @Override
    public OperationDAL map(Operation operation) {
        if (operation == null)
            return null;

        return OperationDAL.builder()
                .id(operation.getId())
                .nameOperation(operation.getNameOperation())
                .categoryDAL(categoryDALMapper.map(operation.getCategory()))
                .value(operation.getValue())
                .dateOperation(operation.getDateOperation())
                .build();
    }

    @Override
    public Operation map(OperationDAL obj) {
        if (obj == null)
            return null;

        return Operation.builder()
                .id(obj.getId())
                .nameOperation(obj.getNameOperation())
                .category(categoryDALMapper.map(obj.getCategoryDAL()))
                .value(obj.getValue())
                .dateOperation(obj.getDateOperation())
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
