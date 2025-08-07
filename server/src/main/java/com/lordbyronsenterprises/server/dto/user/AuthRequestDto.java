package com.lordbyronsenterprises.server.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AuthRequestDto {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
