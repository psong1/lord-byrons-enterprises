package com.lordbyronsenterprises.server.user;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class UpdateUserDto {
    private String firstName;
    private String lastName;

    @Size(min = 4)
    private String username;

    @Email(message = "Invalid email")
    private String email;

}
