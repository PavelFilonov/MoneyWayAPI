package com.edu.moneywayapi.webApi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String login;
    private String password;
    private List<GroupDTO> groups;
    private List<CategoryDTO> categories;
}
