package Him.admin.Models;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "branches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Branch name is required")
    @Size(min = 2, max = 100, message = "Branch name must be between 2 and 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String branchName;

    @NotBlank(message = "Branch address is required")
    @Size(min = 5, max = 255, message = "Branch address must be between 5 and 255 characters")
    @Column(nullable = false, length = 255)
    private String branchAddress;

    @NotBlank(message = "Branch phone is required")
    @Pattern(regexp = "^[0-9+\\-() ]{7,20}$", message = "Invalid phone number format")
    @Column(nullable = false, length = 20)
    private String branchPhone;

    @NotBlank(message = "Branch email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 100)
    private String branchEmail;

    @NotBlank(message = "Branch code is required")
    @Size(min = 2, max = 10, message = "Branch code must be between 2 and 10 characters")
    @Column(nullable = false, unique = true, length = 10)
    private String branchCode;

    // Relations
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<User> users;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private  Set<ExpenditureHead>  expenditureHeads;

    @OneToMany (mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true) //one branch head can have  many revenueHeads
    private Set<RevenueHeads>  revenueHeads;

    @OneToMany(mappedBy = "branch")
    private Set<Member> members = new HashSet<>();
}
