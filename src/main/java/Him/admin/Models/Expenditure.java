package Him.admin.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenditures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expenditure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;

    @NotNull
    private LocalDateTime expenditureDate;

    @Size(max = 255)
    private String description;

    @ManyToOne
    @JoinColumn(name = "expenditure_head_id")
    private ExpenditureHead expenditureHead;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;
}
