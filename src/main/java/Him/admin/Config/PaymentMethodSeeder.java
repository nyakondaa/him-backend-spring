package Him.admin.Config;

import Him.admin.Models.PaymentMethod;
import Him.admin.Repositories.PaymentMethodRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentMethodSeeder {

    private final PaymentMethodRepository paymentMethodRepository;

    @PostConstruct
    public void seedPaymentMethods() {
        if (paymentMethodRepository.count() > 0) {
            System.out.println("✅ Payment methods already exist. Skipping seeding...");
            return;
        }

        // ============================================================
        // ===== 1️⃣ Define Default Payment Methods =====
        // ============================================================
        PaymentMethod cash = PaymentMethod.builder()
                .name("Cash")
                .details("Physical cash payment")
                .build();

        PaymentMethod ecocash = PaymentMethod.builder()
                .name("Ecocash")
                .details("Mobile money payment via Ecocash")
                .build();

        PaymentMethod bankTransfer = PaymentMethod.builder()
                .name("Bank Transfer")
                .details("Payment made through bank transfer")
                .build();



        PaymentMethod online = PaymentMethod.builder()
                .name("Online")
                .details("Online payment (e.g., Paynow, Vpayments)")
                .build();

        // ============================================================
        // ===== 2️⃣ Save Payment Methods =====
        // ============================================================
        paymentMethodRepository.saveAll(List.of(cash, ecocash, bankTransfer, online));

        System.out.println("✅ Default payment methods seeded successfully!");
    }
}
