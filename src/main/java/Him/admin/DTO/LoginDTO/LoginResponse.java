package Him.admin.DTO.LoginDTO;

import Him.admin.Models.Branch;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        String username,
        String role,
        String branch

) {
    public LoginResponse(String accessToken, String refreshToken, Long expiresIn, String username, String role, String branch) {
        this(accessToken, refreshToken, "Bearer", expiresIn, username, role,  branch);
    }
}