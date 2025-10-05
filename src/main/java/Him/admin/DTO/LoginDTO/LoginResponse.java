package Him.admin.DTO.LoginDTO;

import java.util.Set; // Import Set
import java.util.Collections; // Import Collections for safety

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        String firstName,
        String lastName,
        String username,
        String role,
        String branch,
        Set<String> permissions // <-- CHANGED TO Set<String>

) {
    // Corrected convenient constructor including permissions
    public LoginResponse(String accessToken, String refreshToken, Long expiresIn,String firstName, String lastName, String username, String role, String branch, Set<String> permissions) {
        this(accessToken, refreshToken, "Bearer", expiresIn,firstName, lastName, username, role, branch, permissions);
    }

}