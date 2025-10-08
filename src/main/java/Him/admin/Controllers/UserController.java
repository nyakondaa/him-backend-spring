package Him.admin.Controllers;

import Him.admin.DTO.Users.UpdateUserDTO;
import Him.admin.DTO.Users.UserRequestDTO;
import Him.admin.DTO.Users.LoginRequestDTO;
import Him.admin.DTO.Users.UserResponseDTO;
import Him.admin.Exceptions.AuthServiceException;
import Him.admin.Exceptions.UserNotFoundException;
import Him.admin.Models.Role;
import Him.admin.Models.User;
import Him.admin.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1️⃣ Create user
    @PostMapping
    //@PreAuthorize("hasAuthority('users:create')")
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
   // @PreAuthorize("hasAnyAuthority('users:read')")
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
    //@PreAuthorize("hasAnyAuthority('users:read')")
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
//@PreAuthorize("hasAnyAuthority('users:update')")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO dto
    ) {
        try {
            User updated = userService.updateUser(id, dto);

            // Refresh authentication if username changed
            if (dto.username() != null && !dto.username().isBlank()) {
                UserDetails updatedUserDetails = userService.loadUserByUsername(updated.getUsername());
                UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                        updatedUserDetails,
                        null,
                        updatedUserDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }

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

        } catch (UserNotFoundException ex) {
            throw ex; // handled by your global exception handler
        } catch (Exception ex) {
            throw new AuthServiceException("Failed to update user: " + ex.getMessage());
        }
    }


    // 5️⃣ Delete user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('users:delete')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Collections.singletonMap("message", "User deleted successfully"));
    }

    // 6️⃣ Lock user
    @PreAuthorize("hasAnyAuthority('users:create')")
    @PostMapping("/{id}/lock")
    public ResponseEntity<String> lockUser(@PathVariable Long id) {
        userService.lockUser(id);
        return ResponseEntity.ok("User locked");
    }

    // 7️⃣ Unlock user
    @PreAuthorize("hasAnyAuthority('users:create')")
    @PostMapping("/{id}/unlock")
    public ResponseEntity<String> unlockUser(@PathVariable Long id) {
        userService.unlockUser(id);
        return ResponseEntity.ok("User unlocked");
    }

    // Add this to your UserController


    @GetMapping("/debug/authorities")
    @PreAuthorize("isAuthenticated()") // Add this line
    public ResponseEntity<?> debugAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("=== DEBUG CURRENT USER AUTHORITIES ===");
        System.out.println("User: " + auth.getName());
        System.out.println("Authorities: " + auth.getAuthorities());
        System.out.println("Is Authenticated: " + auth.isAuthenticated());
        System.out.println("Principal: " + auth.getPrincipal());
        System.out.println("===============================");

        return ResponseEntity.ok(Map.of(
                "user", auth.getName(),
                "authenticated", auth.isAuthenticated(),
                "authorities", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        ));
    }

    

}
