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

    private final CategoryDTOMapper categoryDTOMapper;
    private final UserDTOMapper userDTOMapper;

    @Autowired
    public OperationDTOMapper(CategoryDTOMapper categoryDTOMapper, UserDTOMapper userDTOMapper) {
        this.categoryDTOMapper = categoryDTOMapper;
        this.userDTOMapper = userDTOMapper;
    }

    @Override
    public OperationDTO map(Operation operation) {
        if (operation == null)
            return null;

        return OperationDTO.builder()
                .id(operation.getId())
                .type(operation.getType())
                .category(categoryDTOMapper.map(operation.getCategory()))
                .user(userDTOMapper.map(operation.getUser()))
                .value(operation.getValue())
                .createdAt(operation.getCreatedAt().toString())
                .build();
    }

    @Override
    public Operation map(OperationDTO obj) {
        if (obj == null)
            return null;

        return Operation.builder()
                .id(obj.getId())
                .type(obj.getType())
                .category(categoryDTOMapper.map(obj.getCategory()))
                .user(userDTOMapper.map(obj.getUser()))
                .value(obj.getValue())
                .createdAt(LocalDateTime.parse(obj.getCreatedAt()))
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
