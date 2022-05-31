package com.edu.moneywayapi.webApi.context;

import com.edu.moneywayapi.webApi.dto.CategoryDTO;
import com.edu.moneywayapi.webApi.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCategoryContext {
    private UserDTO user;
    private CategoryDTO category;
}
