package com.edu.moneywayapi.webApi.dto;

import com.edu.moneywayapi.domain.entity.TypeOperation;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private String createdAt;
}
