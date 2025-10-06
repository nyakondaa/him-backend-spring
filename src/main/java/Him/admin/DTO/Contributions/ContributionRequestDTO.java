package Him.admin.DTO.Contributions;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ContributionRequestDTO(
        @NotNull(message = "Project ID is required")
        Long projectId,

        @NotNull(message = "Member ID is required")
        Long memberId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,



        @NotNull(message = "Branch ID is required")
        Long branchId,

        @NotNull(message = "Payment method ID is required")
        Long paymentMethodId,

        Long processedByUserId


) {}