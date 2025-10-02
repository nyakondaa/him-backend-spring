package Him.admin.Controllers;

import Him.admin.DTO.UserRequestDTO;
import Him.admin.DTO.LoginRequestDTO;
import Him.admin.DTO.UserResponseDTO;
import Him.admin.Models.Role;
import Him.admin.Models.User;
import Him.admin.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1️⃣ Create user
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        User user = userService.createUser(
                dto.getUsername(),
                dto.getPassword(),
                dto.getEmail(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getBranchId(),  // this is Long
                dto.getRoles() != null ? dto.getRoles() : Set.of() // this is Set<String>
        );


        UserResponseDTO response = new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getBranch() != null ? user.getBranch().getBranchName() : null,
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );

        return ResponseEntity.ok(response);
    }

    // 2️⃣ Get all users
    @GetMapping
    public ResponseEntity<Set<UserResponseDTO>> getAllUsers() {
        Set<UserResponseDTO> users = userService.findAll()
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getBranch() != null ? user.getBranch().getBranchName() : null,
                        user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
                ))
                .collect(Collectors.toSet());
        return ResponseEntity.ok(users);
    }

    // 3️⃣ Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getBranch() != null ? user.getBranch().getBranchName() : null,
                        user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4️⃣ Update user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO dto
    ) {
        User updated = userService.updateUser(id, dto);
        UserResponseDTO response = new UserResponseDTO(
                updated.getId(),
                updated.getUsername(),
                updated.getEmail(),
                updated.getFirstName(),
                updated.getLastName(),
                updated.getBranch() != null ? updated.getBranch().getBranchName() : null,
                updated.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
        return ResponseEntity.ok(response);
    }

    // 5️⃣ Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    // 6️⃣ Lock user
    @PostMapping("/{id}/lock")
    public ResponseEntity<String> lockUser(@PathVariable Long id) {
        userService.lockUser(id);
        return ResponseEntity.ok("User locked");
    }

    // 7️⃣ Unlock user
    @PostMapping("/{id}/unlock")
    public ResponseEntity<String> unlockUser(@PathVariable Long id) {
        userService.unlockUser(id);
        return ResponseEntity.ok("User unlocked");
    }

    // 8️⃣ Check permission
    @GetMapping("/{id}/permission")
    public ResponseEntity<Boolean> hasPermission(
            @PathVariable Long id,
            @RequestParam String module,
            @RequestParam String action
    ) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(userService.hasPermission(user, module, action)))
                .orElse(ResponseEntity.notFound().build());
    }

    // 9️⃣ Login
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO dto) {
        boolean success = userService.authenticate(dto.getUsername(), dto.getPassword());
        if (success) return ResponseEntity.ok("Login successful");
        return ResponseEntity.status(401).body("Invalid credentials or account locked");
    }
}
