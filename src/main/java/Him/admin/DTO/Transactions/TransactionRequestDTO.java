package Him.admin.DTO.Transactions;

import Him.admin.Models.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequestDTO(


        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Transaction date is required")
        LocalDateTime transactionDate,

        @NotBlank(message = "Currency is required")
        String currency,
        @NotNull(message = "Payment method ID is required")
        Long paymentMethodId, // <--- NEW REQUIRED FIELD

        @NotNull(message = "member ID is required")
        Long memberId,

        @NotNull(message = "Revenue head ID is required")
        Long revenueHeadId,

        @NotNull(message = "Branch ID is required")
        Long branchId,

        @NotNull(message = "Processed by user ID is required")
        Long userId



) {
}
