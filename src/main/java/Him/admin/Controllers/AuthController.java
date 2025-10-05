package Him.admin.Controllers;

import Him.admin.DTO.LoginDTO.LoginDTORequest;
import Him.admin.DTO.LoginDTO.LoginResponse;
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

            System.out.println(user);


            Set<String> permissions = jwtService.getPermissionsFromUser(user); // ⬅️ ASSUMING THIS METHOD IS NOW PUBLIC

            // 2. Get the role name
            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(authority -> authority.getAuthority().replace("ROLE_", "")) // Remove ROLE_ prefix
                    .orElse("USER");

            // 3. Generate the rich access token
            String accessToken = jwtService.generateTokenForUser(user);

            System.out.println(accessToken);

            // 4. Get refresh token
            var refreshTokenEntity = refreshTokenService.createRefreshToken(user.getId());
            String refreshToken = refreshTokenEntity.getToken();

            // 5. Create response
            LoginResponse response = new LoginResponse(
                    accessToken,
                    refreshToken,
                    jwtService.getJwtExpiration() / 1000L, // Convert milliseconds to seconds
                    user.getFirstName(),
                    user.getLastName(),
                    userDetails.getUsername(),
                    role,
                    user.getBranch() != null ? user.getBranch().getBranchCode() : null, // Branch Code String
                    permissions // ⬅️ PASS THE PERMISSIONS SET HERE
            );
            System.out.println(response);

            return ResponseEntity.ok(response);


        } catch (Exception e) {
            System.err.println("❌ Login failed: " + e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }
}