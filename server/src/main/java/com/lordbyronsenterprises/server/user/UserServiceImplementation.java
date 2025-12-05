package com.lordbyronsenterprises.server.user;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.owasp.encoder.Encode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public UserDto createUser(CreateUserDto dto) {
        // Create user entity manually to ensure correct field mapping
        User user = new User();
        user.setFirstName(Encode.forHtml(dto.getFirstName()));
        user.setLastName(Encode.forHtml(dto.getLastName()));
        user.setUsername(Encode.forHtml(dto.getUsername())); // Explicitly set username from DTO
        user.setEmail(dto.getEmail()); // Explicitly set email from DTO
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole() != null ? dto.getRole() : Role.CUSTOMER);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateUser(String username, UpdateUserDto dto) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        
        if (dto.getRole() != null) {
            throw new IllegalArgumentException("Role cannot be updated through this endpoint. Use the dedicated role update endpoint.");
        }
        
        user.setFirstName(Encode.forHtml(dto.getFirstName()));
        user.setLastName(Encode.forHtml(dto.getLastName()));
        user.setEmail(dto.getEmail());
        if (dto.getUsername() != null) {
            user.setUsername(Encode.forHtml(dto.getUsername()));
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserDto updateUserRole(Long id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public void updatePassword(String username, UpdatePasswordDto dto) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Old password does not match");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
