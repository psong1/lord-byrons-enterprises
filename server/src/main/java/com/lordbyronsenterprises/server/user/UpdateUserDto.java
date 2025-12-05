package com.lordbyronsenterprises.server.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDto {
    private String firstName;
    private String lastName;

    @Size(min = 4)
    private String username;

    @Email(message = "Invalid email")
    private String email;

    private Role role;
}
