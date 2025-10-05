package Him.admin.Repositories;

import Him.admin.Models.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod,Long> {
    boolean existsByName(@NotBlank(message = "Payment method name is required") @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters") String name);
}
