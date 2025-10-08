package Him.admin.DTO.Users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.util.Set;

public record UpdateUserDTO(
        String username,

        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters, include uppercase, lowercase, number, and special character"
        )
        String password,

        @Email(message = "Invalid email format")
        String email,

        Set<String> roles,
        String firstName,
        String lastName,
        Long branchId
) {}
