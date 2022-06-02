package com.edu.moneywayapi.webApi.response;

import com.edu.moneywayapi.webApi.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private UserDTO user;
    private String token;
}
