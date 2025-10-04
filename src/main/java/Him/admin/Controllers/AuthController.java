package Him.admin.Controllers;

import Him.admin.DTO.LoginDTO.*;
import Him.admin.Models.User;
import Him.admin.Services.JWTService;
import Him.admin.Services.RefreshTokenService;
import Him.admin.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTORequest loginRequest) {
        try {
            // Validate input
            if (loginRequest.username() == null || loginRequest.username().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Username is required"));
            }
            if (loginRequest.password() == null || loginRequest.password().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Password is required"));
            }

            System.out.println("🔐 Login attempt for user: " + loginRequest.username());
            System.out.println("loginRequest pass(): " + loginRequest.password());

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            // Get user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            System.out.println(userDetails.getUsername());

            // Load user from database to get additional info
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found in database"));

            System.out.println("✅ Login successful for user: " + user.getUsername());

            // Check if user is active and not locked
            if (!user.isEnabled()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Account is disabled"));
            }
            if (!user.isAccountNonLocked()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Account is locked"));
            }

            // Prepare JWT claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("username", user.getUsername());
            claims.put("email", user.getEmail());




            // Generate tokens
            String accessToken = jwtService.generateTokenWithClaims(userDetails, claims);
            var refreshToken = refreshTokenService.createRefreshToken(user.getId());

            // Create response
            LoginResponse response = new LoginResponse(
                    accessToken,
                    refreshToken.getToken(),
                    jwtService.getJwtExpiration() / 1000, // Convert to seconds
                    user.getUsername(),
                    (String) claims.get("roleName")
            );

            System.out.println("🎯 Generated access token for user: " + user.getUsername());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            System.err.println("❌ Invalid credentials for user: " + loginRequest.username());
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid username or password"));
        } catch (Exception e) {
            System.err.println("💥 Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            if (request.refreshToken() == null || request.refreshToken().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Refresh token is required"));
            }

            System.out.println("🔄 Refresh token attempt");

            var refreshTokenOpt = refreshTokenService.findByToken(request.refreshToken());
            if (refreshTokenOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid refresh token"));
            }

            var refreshToken = refreshTokenService.verifyExpiration(refreshTokenOpt.get());
            User user = refreshToken.getUser();

            // Load fresh user details
            UserDetails userDetails = userService.loadUserByUsername(user.getUsername());

            // Prepare JWT claims for new token
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("username", user.getUsername());
            claims.put("email", user.getEmail());

            // Handle roles
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                String primaryRole = user.getRoles().stream()
                        .findFirst()
                        .map(role -> role.getName())
                        .orElse("USER");
                claims.put("roleName", primaryRole);
                claims.put("role", primaryRole);
            } else {
                claims.put("roleName", "USER");
                claims.put("role", "USER");
            }

            // Handle branch info
            if (user.getBranch() != null) {
                claims.put("branch", user.getBranch().getBranchName());
                claims.put("branchCode", user.getBranch().getBranchCode());
            }

            // Generate new access token
            String newAccessToken = jwtService.generateTokenWithClaims(userDetails, claims);

            LoginResponse response = new LoginResponse(
                    newAccessToken,
                    request.refreshToken(), // Return same refresh token
                    jwtService.getJwtExpiration() / 1000,
                    user.getUsername(),
                    (String) claims.get("roleName")
            );

            System.out.println("✅ Token refreshed for user: " + user.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("💥 Refresh token error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Token refresh failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestBody LogoutRequest request) {
        try {
            if (request.refreshToken() == null || request.refreshToken().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Refresh token is required"));
            }

            refreshTokenService.revokeByToken(request.refreshToken());
            System.out.println("🚪 User logged out successfully");

            return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
        } catch (Exception e) {
            System.err.println("💥 Logout error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Logout failed: " + e.getMessage()));
        }
    }

    // Health check endpoint
    @PostMapping("/test")
    public ResponseEntity<MessageResponse> test() {
        return ResponseEntity.ok(new MessageResponse("Auth endpoint is working!"));
    }
}