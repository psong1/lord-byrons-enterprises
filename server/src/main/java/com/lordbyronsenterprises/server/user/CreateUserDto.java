package com.lordbyronsenterprises.server.user;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class CreateUserDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 4)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
            message = "Passwords must be at least 8 characters and include a digit, a lowercase and an uppercase letter")
    private String password;

    private  Role role;
}
