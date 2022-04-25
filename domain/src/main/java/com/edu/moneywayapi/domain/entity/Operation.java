package com.edu.moneywayapi.domain.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Operation {
    private Long id;
    private NameOperation nameOperation;
    private Category category;
    private String value;
}
