package com.lordbyronsenterprises.server.user;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String token;
    private String username;
    private String role;
}
