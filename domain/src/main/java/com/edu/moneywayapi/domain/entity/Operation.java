package com.edu.moneywayapi.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Operation {
    private Long id;
    private TypeOperation type;
    private Category category;
    private String value;
    private LocalDateTime dateOperation;
}
