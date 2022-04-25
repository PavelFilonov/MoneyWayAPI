package com.edu.moneywayapi.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class User {
    private Long id;
    private String email;
    private String login;
    private String password;
    private List<Group> groups;
    private List<Category> categories;
}
