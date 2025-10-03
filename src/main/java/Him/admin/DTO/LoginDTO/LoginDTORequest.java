package Him.admin.DTO.LoginDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginDTORequest(
        @NotNull
        @NotBlank
        String username,

        @NotBlank
        String password


) {
}
