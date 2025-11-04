package com.lordbyronsenterprises.server.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.owasp.encoder.Encode;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserImplementation {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setFirstName(Encode.forHtml(user.getFirstName()));
        user.setLastName(Encode.forHtml(user.getLastName()));
        user.setUsername(Encode.forHtml(user.getUsername()));

        return userRepository.save(user);
    }

    public User updateUser(String username, User updatedUser) {
        return userRepository.findUserByUsername(username)
                .map(existing -> {
                    existing.setFirstName(Encode.forHtml(updatedUser.getFirstName()));
                    existing.setLastName(Encode.forHtml(updatedUser.getLastName()));
                    existing.setEmail(updatedUser.getEmail());
                    // If you later add password updates, remember to encode here as well.
                    if (updatedUser.getUsername() != null) {
                        existing.setUsername(Encode.forHtml(updatedUser.getUsername()));
                    }
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
