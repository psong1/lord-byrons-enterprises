package com.lordbyronsenterprises.server.user;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AuthRequestDto {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
