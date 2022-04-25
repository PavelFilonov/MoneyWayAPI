package com.edu.moneywayapi.webApi.mapper;

import com.edu.moneywayapi.domain.entity.Operation;
import com.edu.moneywayapi.domain.mapper.OperationMapper;
import com.edu.moneywayapi.webApi.dto.OperationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class OperationDTOMapper implements OperationMapper<OperationDTO> {

    @Autowired
    private CategoryDTOMapper categoryDTOMapper;

    @Override
    public OperationDTO map(Operation operation) {
        if (operation == null)
            return null;

        return OperationDTO.builder()
                .id(operation.getId())
                .nameOperation(operation.getNameOperation())
                .categoryDTO(categoryDTOMapper.map(operation.getCategory()))
                .value(operation.getValue())
                .dateOperation(operation.getDateOperation().toString())
                .build();
    }

    @Override
    public Operation map(OperationDTO obj) {
        if (obj == null)
            return null;

        return Operation.builder()
                .id(obj.getId())
                .nameOperation(obj.getNameOperation())
                .category(categoryDTOMapper.map(obj.getCategoryDTO()))
                .value(obj.getValue())
                .dateOperation(LocalDateTime.parse(obj.getDateOperation()))
                .build();
    }

    public List<OperationDTO> mapListToDTO(List<Operation> operations) {
        List<OperationDTO> operationsDTO = new ArrayList<>();

        for (Operation operation : operations) {
            operationsDTO.add(map(operation));
        }

        return operationsDTO;
    }
}
