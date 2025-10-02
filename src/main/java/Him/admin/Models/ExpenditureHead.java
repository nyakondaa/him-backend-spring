package Him.admin.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "expenditure_heads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenditureHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Expenditure head name is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Expenditure code is required")
    @Size(min = 5, max = 10)
    @Column(nullable = false, unique = true)
    private String code; // BBEEE

    @Size(max = 255)
    private String description;

    // Relations
    @OneToMany(mappedBy = "expenditureHead", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Expenditure> expenditures;
}
