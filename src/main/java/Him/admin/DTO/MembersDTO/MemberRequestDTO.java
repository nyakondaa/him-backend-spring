package Him.admin.DTO.MembersDTO;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

public record MemberRequestDTO(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        LocalDate birthDate,

        @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
        String gender,

        String address,

        @NotBlank(message = "Phone is required")
        @Size(min = 10, max = 20)
        String phone,

        @Email(message = "Invalid email format")
        String email,


        @NotNull(message = "Branch is required")
        Long branchId



) {}
