package Him.admin.DTO.PaymentsDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PaymentMethodRequestDTO(
        @NotBlank(message = "Payment method name is required")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @Size(max = 100, message = "Details cannot exceed 100 characters")
        String details
) {}