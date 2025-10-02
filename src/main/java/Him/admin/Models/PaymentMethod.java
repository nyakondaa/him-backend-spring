package Him.admin.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Payment method name is required")
    @Size(min = 2, max = 50)
    @Column(nullable = false, unique = true)
    private String name; // e.g., Cash, Ecocash, Bank

    @Size(max = 100)
    private String details; // e.g., bank name

    @OneToMany(mappedBy = "paymentMethod")
    private Set<Transaction> transactions;
}
