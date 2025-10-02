package Him.admin.DTO.Ependitures;

import java.time.LocalDate;

public record ExpenditureResponseDTO(
         Long id,
         String description,
         Double amount,
         LocalDate date,
         String branchName
) {
}
