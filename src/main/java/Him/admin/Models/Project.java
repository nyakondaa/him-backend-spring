package Him.admin.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal fundingGoal;

    @NotNull
    @Builder.Default
    @DecimalMin("0.0")
    private BigDecimal currentFunding = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.DRAFT;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @NotNull
    private Branch branch;

    // Relationship with transactions
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper method to add transaction and update funding
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setProject(this);
        this.currentFunding = this.currentFunding.add(transaction.getAmount());

        // Auto-update status if funding goal is reached
        if (this.currentFunding.compareTo(this.fundingGoal) >= 0) {
            this.status = ProjectStatus.COMPLETED;
        }
    }

    public enum ProjectStatus {
        DRAFT, ACTIVE, COMPLETED, CANCELLED
    }
}