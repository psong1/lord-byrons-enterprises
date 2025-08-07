package com.lordbyronsenterprises.server.dto.user;

import lombok.Data;
import com.lordbyronsenterprises.server.model.Role;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Role role;
}
