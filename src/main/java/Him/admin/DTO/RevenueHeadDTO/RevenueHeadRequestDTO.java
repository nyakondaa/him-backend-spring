package Him.admin.DTO.RevenueHeadDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RevenueHeadRequestDTO(
        @NotBlank
        String name,

        @NotBlank
        String code,

        @NotBlank
        String description,

        @NotNull
        Long branchID
) {
}
