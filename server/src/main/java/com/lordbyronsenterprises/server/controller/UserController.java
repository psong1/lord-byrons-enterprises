package com.lordbyronsenterprises.server.controller;

import com.lordbyronsenterprises.server.dto.user.CreateUserDto;
import com.lordbyronsenterprises.server.dto.user.UpdateUserDto;
import com.lordbyronsenterprises.server.dto.user.UserDto;
import com.lordbyronsenterprises.server.mapper.UserMapper;
import com.lordbyronsenterprises.server.model.User;
import com.lordbyronsenterprises.server.repository.UserRepository;
import com.lordbyronsenterprises.server.service.user.UserImplementation;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserImplementation userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserController(UserImplementation userService, UserRepository userRepository, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAllUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDto> register(@Valid @RequestBody CreateUserDto dto) {
        try {
            User user = new User();
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            // Pass raw password; service will hash using BCrypt
            user.setPassword(dto.getPassword());
            user.setRole(dto.getRole());
            User saved = userService.createUser(user);
            return ResponseEntity.status(201).body(userMapper.toDto(saved));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDto> update(@PathVariable String username, @Valid @RequestBody UpdateUserDto dto) {
        try {
            User patch = new User();
            patch.setFirstName(dto.getFirstName());
            patch.setLastName(dto.getLastName());
            patch.setEmail(dto.getEmail());
            if (dto.getUsername() != null) {
                patch.setUsername(dto.getUsername());
            }
            User updated = userService.updateUser(username, patch);
            return ResponseEntity.ok(userMapper.toDto(updated));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
