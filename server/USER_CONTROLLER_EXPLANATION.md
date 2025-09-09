# UserController: What Was Added and Why (E‑commerce Focus)

This document explains the endpoints, wiring, and design decisions in `UserController`, and why these are appropriate for an e‑commerce application. It also includes a draft plan for migrating password hashing to BCrypt in the service layer (without implementing it yet).

Repository paths of interest:
- Controller: `src/main/java/com/lordbyronsenterprises/server/controller/UserController.java`
- DTOs: `src/main/java/com/lordbyronsenterprises/server/dto/user/*`
- Service: `src/main/java/com/lordbyronsenterprises/server/service/user/UserImplementation.java`
- Repository: `src/main/java/com/lordbyronsenterprises/server/repository/UserRepository.java`
- Mapper: `src/main/java/com/lordbyronsenterprises/server/mapper/UserMapper.java`

## 1) Constructor Injection and Dependencies

Added fields and constructor dependencies:
- `UserImplementation userService`
- `UserRepository userRepository`
- `UserMapper userMapper`

Why:
- `UserImplementation` exposes the concrete methods that exist today (getAllUsers, createUser, updateUser(username,…), deleteUserById). The interface `UserService` has different signatures and is not implemented by the concrete class, so using the implementation avoids a broader refactor.
- `UserRepository` is used where direct lookups are necessary and not exposed as public methods on the service (e.g., `findById` for GET by id).
- `UserMapper` converts between domain `User` and `UserDto`, keeping the controller thin.

E‑commerce rationale:
- Predictable, testable constructors are standard. Thin controllers reduce coupling and keep business rules in the service/repository layers, which aligns with common e‑commerce architectures.

## 2) Base Route and Style

- `@RestController` and `@RequestMapping("/user")` mirror the project’s existing controller style (see `ProductController`).
- All endpoints return `ResponseEntity<…>` for explicit HTTP status control.

E‑commerce rationale:
- Consistent resource naming and status control are important for API consumers (web/mobile storefronts, admin panels).

## 3) GET /user — List All Users

```java
@GetMapping
public List<UserDto> getAll() {
    return userService.getAllUsers()
            .stream()
            .map(userMapper::toDto)
            .toList();
}
```

Why:
- Provides a list endpoint akin to products/categories.
- Uses the mapper to avoid leaking entity internals.

E‑commerce rationale:
- Admin views often need user lists for customer support, fraud checks, or marketing segments.

## 4) GET /user/{id} — Get User by ID

```java
@GetMapping("/{id}")
public ResponseEntity<UserDto> getById(@PathVariable Long id) {
    return userRepository.findById(id)
            .map(userMapper::toDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

Why:
- Fetch by id via repository (service’s `getUserById` is not public). Minimal change approach.

E‑commerce rationale:
- Support agents or order workflows often need precise user retrieval by primary key.

## 5) POST /user — Register User

```java
@PostMapping
public ResponseEntity<UserDto> register(@Valid @RequestBody CreateUserDto dto) {
    try {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        // Hash password before saving (simple SHA-256 for this draft; consider BCrypt)
        user.setPassword(hashPassword(dto.getPassword()));
        user.setRole(dto.getRole());
        User saved = userService.createUser(user);
        return ResponseEntity.status(201).body(userMapper.toDto(saved));
    } catch (DataIntegrityViolationException e) {
        return ResponseEntity.status(409).build();
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().build();
    }
}
```

What and why:
- Accepts `CreateUserDto` with validation (`@Valid`).
- Populates `User` from DTO and hashes the password before saving (temporary SHA‑256 helper to avoid new dependencies; see BCrypt draft below).
- Returns `201 Created` on success, `409 Conflict` on unique constraint violations (e.g., username/email already taken), `400 Bad Request` on other failures.

E‑commerce rationale:
- Strong validation at the edge is essential for user sign‑up flows.
- Clear status codes improve UX and platform integration (e.g., show “username already taken”).

Note: The `User` entity requires an email; we added `email` into `CreateUserDto` and wired it through.

## 6) PUT /user/{username} — Update Partial User Fields

```java
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
```

What and why:
- Path key is `username` because the service updates by username.
- Accepts optional fields and delegates merging to the service.
- Handles `409` for conflicts (e.g., changing username/email to an existing one) and `404` for not found.

E‑commerce rationale:
- Customer profile edits are common. Returning appropriate statuses improves client logic (e.g., “email already in use”).

## 7) DELETE /user/{id} — Delete User

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    try {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
        return ResponseEntity.notFound().build();
    }
}
```

Why:
- Provides removal endpoint. Uses `204 No Content` which is common for successful deletes.

E‑commerce rationale:
- Admin workflows may need to disable/delete accounts (subject to policy and data retention rules).

## 8) Validation and Status Codes

- `@Valid` is added to `register` and `update` to leverage constraints on DTOs.
- `409 Conflict` is returned on `DataIntegrityViolationException` (likely unique constraints for username/email).
- `201 Created` for successful registration and `204 No Content` for successful delete.

E‑commerce rationale:
- Better status semantics lead to clearer client behavior (checkout, signup/login flows, admin tools).

## 9) Mapper Notes

`UserMapper` currently maps id, firstName, lastName, email (and not username/role). We kept the mapper unchanged to minimize scope. If needed, it can be extended so `UserDto` includes username/role in responses.

E‑commerce rationale:
- Minimizing change reduces risk. Mapper enhancements can be a safe follow-up if UI needs more fields.

---

# Draft: Migrate Password Hashing to BCrypt in the Service (Do Not Implement Yet)

This section shows how you can move from the temporary SHA‑256 helper in the controller to a robust BCrypt approach handled in the service layer. This is a draft you can apply yourself.

## 1) Add dependency (pom.xml)

```xml
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-crypto</artifactId>
</dependency>
```

## 2) Provide a PasswordEncoder bean (e.g., in a config class)

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityCryptoConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## 3) Inject PasswordEncoder into UserImplementation and hash on create

```java
@Service
public class UserImplementation {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // add this

    public UserImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        // Hash the raw password passed in from the controller/DTO
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
        return userRepository.save(user);
    }

    public User updateUser(String username, User updatedUser) {
        return userRepository.findUserByUsername(username)
            .map(existing -> {
                existing.setFirstName(updatedUser.getFirstName());
                existing.setLastName(updatedUser.getLastName());
                existing.setEmail(updatedUser.getEmail());
                if (updatedUser.getUsername() != null) {
                    existing.setUsername(updatedUser.getUsername());
                }
                // If you later allow password updates via UpdateUserDto, remember:
                // existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                return userRepository.save(existing);
            })
            .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
}
```

## 4) Simplify controller (remove SHA‑256 helper)

In `UserController.register`, stop hashing and pass the raw password:

```java
user.setPassword(dto.getPassword()); // no hash here
```

Remove the `hashPassword` helper method from the controller.

## 5) Why BCrypt in the service?
- Centralizes security logic where persistence occurs, reducing risks of inconsistent hashing.
- BCrypt includes a salt and is intentionally slow, which helps mitigate brute force attacks.
- Easier future tuning (e.g., encoder strength) without changing controllers.

---

## Potential Next Steps
- Add pagination to `GET /user` (page, size) for large user bases.
- Extend `UserMapper` to include username and role in `UserDto` responses.
- Add global exception handling (e.g., `@ControllerAdvice`) for consistent error payloads.
- Implement login with password check and JWT issuance (separate auth service), returning a token in `AuthResponseDto`.
