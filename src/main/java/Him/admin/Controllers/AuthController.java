package Him.admin.Controllers;

import Him.admin.DTO.LoginDTO.LoginDTORequest;
import Him.admin.DTO.LoginDTO.LoginResponse;
import Him.admin.Exceptions.AuthServiceException;
import Him.admin.Exceptions.InvalidCredentialsException;
import Him.admin.Exceptions.UserNotFoundException;
import Him.admin.Models.Role;
import Him.admin.Models.User;
import Him.admin.Services.JWTService;
import Him.admin.Services.RefreshTokenService;
import Him.admin.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
                // Authenticate user
                Authentication auth = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
                );

                UserDetails userDetails = (UserDetails) auth.getPrincipal();

                // Load user entity
                User user = userService.findByUsername(userDetails.getUsername())
                        .orElseThrow(() -> new UserNotFoundException("User not found"));

                // Get role
                String role = user.getRoles().stream()
                        .findFirst()
                        .map(Role::getName)
                        .orElse("USER");

                // Get permissions
                Set<String> permissions = jwtService.getPermissionsFromUser(user);

                // Generate tokens
                String accessToken = jwtService.generateTokenForUser(user);
                String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

                // Build response
                LoginResponse response = new LoginResponse(
                        accessToken,
                        refreshToken,
                        jwtService.getJwtExpiration() / 1000L,
                        user.getFirstName(),
                        user.getLastName(),
                        userDetails.getUsername(),
                        role,
                        user.getBranch() != null ? user.getBranch().getBranchCode() : null,
                        permissions
                );

                return ResponseEntity.ok(response);

            } catch (BadCredentialsException ex) {
                throw new InvalidCredentialsException("Invalid username or password");
            } catch (UserNotFoundException ex) {
                throw ex; // already meaningful
            } catch (Exception ex) {
                throw new AuthServiceException("Login failed: " + ex.getMessage());
            }
        }
    }




