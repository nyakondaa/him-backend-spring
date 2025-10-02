package Him.admin.DTO.Branches;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BranchRequest(
        @NotBlank(message = "Branch name is required")
        @Size(min = 2, max = 100) String branchName,

        @NotBlank(message = "Branch address is required")
        @Size(min = 5, max = 255) String branchAddress,

        @NotBlank(message = "Branch phone is required")
        @Pattern(regexp = "^[0-9+\\-() ]{7,20}$") String branchPhone,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Branch email is required") String branchEmail,

        @NotBlank(message = "Branch code is required")
        @Size(min = 2, max = 10) String branchCode
) {
}
