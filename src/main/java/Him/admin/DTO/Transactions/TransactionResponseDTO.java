package Him.admin.DTO.Transactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(

        String rrn,
        BigDecimal amount,
        LocalDateTime transactionDate,
        String currency,
        String payerName,
        String revenueHeadName,
        String branchName,
        String processedBy


) {
}
