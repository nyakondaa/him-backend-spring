package Him.admin.Controllers;

import Him.admin.DTO.LoginDTO.LoginDTORequest;
import Him.admin.DTO.LoginDTO.LoginResponse;
import Him.admin.Services.JWTService;
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
public class AuthControllerr {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginDTORequest dto) throws Exception {
        Authentication auth = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        UserDetails user = (UserDetails) auth.getPrincipal();

        // generate JWT
        String token = jwtService.generateToken(user);
        auth.getAuthorities().forEach(a -> System.out.println(a.getAuthority()));


        return ResponseEntity.ok(new LoginResponse(token));

    }
}
