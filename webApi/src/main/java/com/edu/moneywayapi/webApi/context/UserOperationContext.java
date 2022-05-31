package com.edu.moneywayapi.webApi.context;

import com.edu.moneywayapi.webApi.dto.OperationDTO;
import com.edu.moneywayapi.webApi.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserOperationContext {
    private UserDTO user;
    private OperationDTO operation;
}
