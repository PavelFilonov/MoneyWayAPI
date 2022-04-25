package com.edu.moneywayapi.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Group {
    private Long id;
    private String name;
    private String token;
    private Long ownerId;
    private List<Category> categories;
}
