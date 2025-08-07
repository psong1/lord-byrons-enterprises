package com.lordbyronsenterprises.server.service.user;

import com.lordbyronsenterprises.server.model.User;
import com.lordbyronsenterprises.server.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserImplementation {

    private final UserRepository userRepository;

    public UserImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    Optional<User> getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(String username, User updatedUser) {
        return userRepository.findUserByUsername(username)
                .map(existing -> {
                    existing.setFirstName(updatedUser.getFirstName());
                    existing.setLastName(updatedUser.getLastName());
                    existing.setEmail(updatedUser.getEmail());
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
