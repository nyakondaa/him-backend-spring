package Him.admin.DTO.ExpenditureHeadsDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ExpenditureHeadsResponse(
        Long id,
        String name,
        String code, // BBEEE
        String description

) {
}
