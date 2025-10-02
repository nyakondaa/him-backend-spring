package Him.admin.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;

    @NotNull
    private LocalDateTime transactionDate;

    @NotBlank
    private String currency; // ZIG, USD, etc.

    @NotBlank
    private String payerName;

    @ManyToOne
    @JoinColumn(name = "revenue_head_id")
    private RevenueHeads revenueHead;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User processedBy;

    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;
}
