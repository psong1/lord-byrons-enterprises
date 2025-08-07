package com.lordbyronsenterprises.server.service.user;

import com.lordbyronsenterprises.server.model.User;
import com.lordbyronsenterprises.server.model.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);

    User updateUserRole(Long userId, Role newRole);
}
