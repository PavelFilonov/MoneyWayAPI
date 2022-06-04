package com.edu.moneywayapi.webApi.dto;

import com.edu.moneywayapi.domain.entity.TypeOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationDTO {
    private Long id;
    private TypeOperation type;
    private CategoryDTO category;
    private UserDTO user;
    private Double value;
    private String createdAt;
}
