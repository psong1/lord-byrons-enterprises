package com.lordbyronsenterprises.server.user;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDto> getAllUsers();
    Optional<UserDto> getUserById(Long id);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    UserDto createUser(CreateUserDto userDto);
    UserDto updateUser(String username, UpdateUserDto userDto);
    void updatePassword(String username, UpdatePasswordDto dto);
    void deleteUserById(Long id);

}
