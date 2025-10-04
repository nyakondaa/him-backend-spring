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

            // Get user entity to get the ID and the Branch
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println(user);

            // Get the first authority as role
            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(authority -> authority.getAuthority())
                    .orElse("USER");

            // Generate tokens
            String accessToken = jwtService.generateToken(userDetails);

            System.out.println(accessToken);

            // Get refresh token - pass user ID, not username
            var refreshTokenEntity = refreshTokenService.createRefreshToken(user.getId());
            String refreshToken = refreshTokenEntity.getToken();

            // Create response
            LoginResponse response = new LoginResponse(
                    accessToken,
                    refreshToken,
                    jwtService.getJwtExpiration() / 1000L, // Convert milliseconds to seconds
                    userDetails.getUsername(),
                    role,
                    user.getBranch().getBranchCode()
            );
            System.out.println(response);

            return ResponseEntity.ok(response);


        } catch (Exception e) {
            System.err.println("❌ Login failed: " + e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }
}