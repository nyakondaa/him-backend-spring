package Him.admin.DTO.Ependitures;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ExpenditureRequestDTO(
        @NotBlank(message = "Description is required")
        @Size(min = 2, max = 100, message = "Description must be between 2 and 100 characters")
         String description,

                @NotNull(message = "Amount is required")
@Positive(message = "Amount must be greater than zero")
 Double amount,

@NotNull(message = "Date is required")
 LocalDate date,

@NotNull(message = "Branch ID is required")
 Long branchId
) {
}
