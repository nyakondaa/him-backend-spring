package Him.admin.DTO.LoginDTO;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        String username,
        String role
) {
    public LoginResponse(String accessToken, String refreshToken, Long expiresIn, String username, String role) {
        this(accessToken, refreshToken, "Bearer", expiresIn, username, role);
    }
}