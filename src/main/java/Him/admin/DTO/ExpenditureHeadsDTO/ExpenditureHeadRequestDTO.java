package Him.admin.DTO.ExpenditureHeadsDTO;

import Him.admin.Models.Expenditure;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ExpenditureHeadRequestDTO(


@NotBlank(message = "Expenditure head name is required")
@Size(min = 2, max = 100)
@Column(nullable = false, unique = true)
 String name,

@NotBlank(message = "Expenditure code is required")
@Size(min = 5, max = 10)

 String code, // BBEEE

@Size(max = 255)
@NotBlank(message = "Expenditure code is required")
 String description,

@NotNull
Long bracnhID

) {
}
