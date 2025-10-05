package Him.admin.Controllers;

import Him.admin.DTO.LoginDTO.LoginDTORequest;
import Him.admin.DTO.LoginDTO.LoginResponse;
import Him.admin.Models.Role;
import Him.admin.Models.User;
import Him.admin.Services.JWTService;
import Him.admin.Services.RefreshTokenService;
import Him.admin.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set; // Import Set for permissions
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginDTORequest dto) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();

            System.out.println("✅ Login successful for user: " + userDetails.getUsername());

            // Get user entity to get the ID and the Branch/Permissions
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("User roles from entity: " +
                    user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

            Set<String> permissions = jwtService.getPermissionsFromUser(user);

            // FIX: Get the role name from the User entity, not from authorities
            String role = user.getRoles().stream()
                    .findFirst()
                    .map(Role::getName) // Get the actual role name from the User entity
                    .orElse("USER");

            // ALTERNATIVE FIX: Filter authorities to only get roles
            // String role = auth.getAuthorities().stream()
            //         .map(GrantedAuthority::getAuthority)
            //         .filter(authority -> authority.startsWith("ROLE_"))
            //         .findFirst()
            //         .map(authority -> authority.replace("ROLE_", ""))
            //         .orElse("USER");

            System.out.println("Selected role for response: " + role);

            // Generate the rich access token
            String accessToken = jwtService.generateTokenForUser(user);

            // Get refresh token
            var refreshTokenEntity = refreshTokenService.createRefreshToken(user.getId());
            String refreshToken = refreshTokenEntity.getToken();

            // Create response
            LoginResponse response = new LoginResponse(
                    accessToken,
                    refreshToken,
                    jwtService.getJwtExpiration() / 1000L,
                    user.getFirstName(),
                    user.getLastName(),
                    userDetails.getUsername(),
                    role, // This should now be "ADMIN"
                    user.getBranch() != null ? user.getBranch().getBranchCode() : null,
                    permissions
            );
            System.out.println("Final response - Role: " + response.role());
            System.out.println("Final response - Permissions: " + response.permissions());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Login failed: " + e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }



}