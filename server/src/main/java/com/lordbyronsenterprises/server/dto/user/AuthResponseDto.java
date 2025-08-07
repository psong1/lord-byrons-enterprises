package com.lordbyronsenterprises.server.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AuthResponseDto {
    private String token;
    private String username;
    private String role;
}
