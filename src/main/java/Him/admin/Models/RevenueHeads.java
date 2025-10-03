package Him.admin.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "revenue_heads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueHeads {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Revenue head name is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Revenue code is required")
    @Size(min = 5, max = 10)
    @Column(nullable = false, unique = true)
    private String code; // BBXXX

    @Size(max = 255)
    private String description;

    // Relations
    @OneToMany(mappedBy = "revenueHead", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;
}
